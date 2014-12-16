String.prototype.trim = function () {
    return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g, "");
};


function supports_html5_storage() {
    try {
        return 'localStorage' in window && window['localStorage'] !== null;
    } catch (e) {
        return false;
    }
}

function getLocalStorage() {
    return window['localStorage'];
}

var wrapToString = function () {
    $(".to-string").each(function () {
        var v = $(this).html();
        $(this).html("<input disabled class='form-control' type='text' value='" + v + "' style='text-align:" + $(this).attr("data-align") + "' />");
    });
};

var delay = (function () {
    var timer = 0;
    return function (callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();

function initConfirm() {
    $("body").on("click", ".confirm-true", function (e) {
        $("#confirmModal").remove();
        e.preventDefault();
        var x = $("<div class='modal fade' id='confirmModal' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>"
                + "<div class='modal-dialog'><div class='modal-content'><div class='modal-header'>"
                + "<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
                + "<h4 class='modal-title'>" + messages["jpm.modal.confirm.title"] + "</h4>"
                + "</div>"
                + "<div class='modal-body'>" + messages["jpm.modal.confirm.text"] + "</div>"
                + "<div class='modal-footer'>"
                + "<button type='button' class='btn btn-default' data-dismiss='modal' >" + messages["jpm.modal.confirm.cancel"] + "</button>"
                + "<a class='btn btn-primary' href='" + $(this).attr("href") + "' >" + messages["jpm.modal.confirm.submit"] + "</button>"
                + "</div></div></div></div>"
                );
        x.appendTo("body");
        x.modal("show");
    });
}

function initWindowsResize() {
    var ul = $('#sidebar > ul');
    var ul2 = $('#sidebar li.open ul');
    // === jPanelMenu === //
    var jPanel = $.jPanelMenu({
        menu: '#sidebar',
        trigger: '#menu-trigger'
    });
    $(window).resize(function () {
        if ($(window).width() > 480 && $(window).width() < 769) {
            ul2.css({'display': 'none'});
            ul.css({'display': 'block'});
        }

        if ($(window).width() <= 480) {
            ul.css({'display': 'none'});
            ul2.css({'display': 'block'});
            if (!$('html').hasClass('jPanelMenu')) {
                jPanel.on();
            }

            if ($(window).scrollTop() > 35) {
                $('body').addClass('fixed');
            }
            $(window).scroll(function () {
                if ($(window).scrollTop() > 35) {
                    $('body').addClass('fixed');
                } else {
                    $('body').removeClass('fixed');
                }
            });
        } else {
            jPanel.off();
        }
        if ($(window).width() > 768) {
            ul.css({'display': 'block'});
            ul2.css({'display': 'block'});
            $('#user-nav > ul').css({width: 'auto', margin: '0'});
        }
    });

    if ($(window).width() <= 480) {
        if ($(window).scrollTop() > 35) {
            $('body').addClass('fixed');
        }
        $(window).scroll(function () {
            if ($(window).scrollTop() > 35) {
                $('body').addClass('fixed');
            } else {
                $('body').removeClass('fixed');
            }
        });
        jPanel.on();
    }

    if ($(window).width() > 480) {
        ul.css({'display': 'block'});
        jPanel.off();
    }
    if ($(window).width() > 480 && $(window).width() < 769) {
        ul2.css({'display': 'none'});
    }
}

function initMenu() {
    $('li.submenu > a').click(function (e) {
        e.preventDefault();
        var submenu = $(this).siblings('ul');
        var li = $(this).parents('li');
        if ($(window).width() > 480) {
            var submenus = $('#sidebar li.submenu ul');
            var submenus_parents = $('#sidebar li.submenu');
        } else {
            var submenus = $('#jPanelMenu-menu li.submenu ul');
            var submenus_parents = $('#jPanelMenu-menu li.submenu');
        }

        if (li.hasClass('open')) {
            if (($(window).width() > 768) || ($(window).width() <= 480)) {
                submenu.slideUp();
            } else {
                submenu.fadeOut(250);
            }
            li.removeClass('open');
        } else {
            if (($(window).width() > 768) || ($(window).width() <= 480)) {
                submenus.slideUp();
                submenu.slideDown();
            } else {
                submenus.fadeOut(250);
                submenu.fadeIn(250);
            }
            submenus_parents.removeClass('open');
            li.addClass('open');
        }
    });
}

$(window).unload(function () {
    $("#loading-div").fadeIn();
});

function initFunctions() {
    $.each(PM_onLoadFunctions, function () {
        try {
            this();
        } catch (e) {
            console.log(e);
            alert("Error: " + e);
        }
    });
}

var uniqBy = function (ary, key) {
    var seen = {};
    return ary.filter(function (elem) {
        var k = key(elem);
        return (seen[k] === 1) ? 0 : seen[k] = 1;
    });
};

var initPage = function () {
    try {
        //Clean empty help-blocks
        $(".help-block:empty").remove();
        $(".panel-body:not(:has(div))").parent(".panel").parent().remove();
        $(".row-fluid:not(:has(div))").remove();
        $(".sortable").click(function () {
            window.location = $(this).attr("data-cp") + $(this).attr("data-entity") + "/sort?fieldId=" + $(this).attr("data-field");
        });
        initConfirm();
        //Init Menu
        initWindowsResize();
        // === Sidebar navigation === //
        initMenu();

        // === Tooltips === //
        $('.tip').tooltip();
        $('.tip-left').tooltip({placement: 'left'});
        $('.tip-right').tooltip({placement: 'right'});
        $('.tip-top').tooltip({placement: 'top'});
        $('.tip-bottom').tooltip({placement: 'bottom'});

        if (currentUser && currentUser !== '') {
            $("#userNavRecent");
            var url = $(location).attr('href');
            if (!url.match(/#$/)) {
                var name = "jpm_recent_" + currentUser;
                var name2 = "jpm_lastUser";
                if ($.cookie(name2) !== currentUser) {
                    $.cookie(name2, currentUser);
                    $.cookie(name, "", {path: '/'});
                }
                var _recentArray = $.cookie(name);
                var recentArray = new Array();
                if (typeof _recentArray !== "undefined" && _recentArray !== "") {
                    recentArray = $.parseJSON(_recentArray);
                }
                var array = Array.prototype.slice.call(recentArray);
                if (array.length >= 10) {
                    array.shift();
                }
                var title = document.title;
                title = title.substring(title.indexOf("- ") + 1);
                array.push({'url': url, 'title': title});
                var finalArray = uniqBy(array, JSON.stringify);
                $.cookie(name, JSON.stringify(finalArray), {path: '/'});
                $.each(finalArray, function (i, item) {
                    $("#userNavRecent").find(".dropdown-menu").append("<li><a title='' href='" + item.url + "'>" + item.title + "</a></li>")
                });
            }
        }

        $("body").on("click", ".inline-boolean", function () {
            var instanceId = $(this).attr("data-id");
            var field = $(this).attr("data-field-name");
            var entity = $(this).attr("data-entity-id");
            var i = $(this).find("span");
            var icon = i.attr("class");
            var iconT = $(this).attr("data-true-icon");
            var iconF = $(this).attr("data-false-icon");
            $.ajax({
                url: contextPath + "jpm/" + entity + "/" + instanceId + "/iledit?name=" + field + "&value=" + ((icon === iconT) ? "" : "1"),
                type: "POST",
                success: function () {
                    if (icon === iconT) {
                        i.removeClass(iconT).addClass(iconF);
                    } else {
                        i.removeClass(iconF).addClass(iconT);
                    }
                }
            });
        });
        initFunctions();
    } finally {
        $("#loading-div").fadeOut();
    }
};

function jpmBlock() {
    $.blockUI({
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: 'none',
            opacity: .6
        },
        overlayCSS: {
            backgroundColor: '#000',
            opacity: .6
        },
        message: '<img style="width: 150px; height: 150px;" src="' + contextPath + 'static/img/main_loading.gif" />'
    });
}

function jpmUnBlock() {
    $.unblockUI();
}

function buildAjaxJpmForm(msgClose) {
    $('#jpmForm').ajaxForm({
        dataType: 'json',
        beforeSubmit: function () {
            jpmBlock();
        },
        success: function (data) {
            console.log(data);
            if (data.ok) {
                document.location = contextPath + data.next;
            } else {
                $(".form-group").removeClass("has-error");
                $(".jpm-validator-text").remove();
                //Entity
                if (data.messages.length > 0) {
                    var $message = '<div><ul>';
                    $.each(data.messages, function (m, text) {
                        $message = "<li>" + text.text + "</li>";
                    });
                    $message = $message + "</ul></div>";
                    BootstrapDialog.show({
                        title: "",
                        message: $($message),
                        type: BootstrapDialog.TYPE_DANGER,
                        buttons: [{
                                label: msgClose,
                                action: function (dialogRef) {
                                    dialogRef.close();
                                    jpmUnBlock();
                                }
                            }]
                    });
                }
                //field
                $.each(data.fieldMessages, function (fieldId, msgs) {
                    var controlGroup = $("#control-group-" + fieldId);
                    controlGroup.addClass("has-error");
                    $.each(msgs, function (i, item) {
                        controlGroup.find(".converted-field-container").append('<p class="help-block jpm-validator-text">' + item.text + '</p>');
                    });
                });
                jpmUnBlock();
            }
        },
        error: function (data) {
            alert("Unexpected error! " + data);
            console.log(data);
            jpmUnBlock();
        }
    });
}

$(window).load(initPage);
