<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.value}">
    <a disabled href="javascript:;" id="field_${field}_${param.instanceId}_${param.objectId}" class="form-control">${param.value}<span class="glyphicon glyphicon-comment pull-right"></span></a>
    <script type="text/javascript">
        $("body").on("click", "#field_${field}_${param.instanceId}_${param.objectId}", function() {
            $.getJSON("${cp}jpm/${param.entityId}/${param.instanceId}/show.json?fields=${param.fields}", function(data) {
                var content = "<table class='table table-compact table-bordered'><tbody>";
                $.each(data, function(i, v) {
                    content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                });
                content = content + "</tbody></table><a href=\"javascript:$('" + "#field_${field}_${param.instanceId}_${param.objectId}" + "').popover('hide');\" class='pull-right'><i class='glyphicon glyphicon-remove'></i></a>";
                //<c:if test="${not empty param.operationLink}">
                content = content + "<a href='${cp}${param.operationLink}'><i class='glyphicon jpmicon-${param.operationId}'></i> ${param.operationTitle}</a>";
                //</c:if>
                $("#field_${field}_${param.instanceId}_${param.objectId}").popover({placement: 'top', html: true, content: content}).popover('show');
            });
        });
    </script>
</c:if>