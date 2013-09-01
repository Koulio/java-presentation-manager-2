<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${param.showBtn}">
    <a id="weak${field}" class="btn btn-default" href="${cp}jpm/${entity.id}/${instance.id}/${param.weakId}/list"><i class="glyphicon ${param.btnIcon}"></i>&nbsp;<spring:message code='${param.btnText}' text='Change' /></a><br/>
</c:if>
<c:if test="${param.showList}">
    <div id="weak${field}-list">
        <img alt="..." src="${cp}static/img/loading.gif"/>
    </div>
    <script type="text/javascript">
        jpmLoad(function() {
            $("#control-group-${field}").find(".col-lg-2").remove();
            $("#control-group-${field}").find(".col-lg-10").removeClass("col-lg-10").addClass("col-lg-12");
            $.ajax({
                url: "${cp}jpm/${entity.id}/${instance.id}/${param.weakId}/weaklist",
                dataType: "text",
                context: document.body,
                success: function(data) {
                    $("#weak${field}-list").html(data).find("table").removeClass().addClass("table table-bordered table-condensed table-striped");
                }
            });
        });
    </script>
</c:if>