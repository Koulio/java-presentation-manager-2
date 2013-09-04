<c:if test="${not empty value}">
    <a href="javascript:;" id="field_${field}_${param.instanceId}_${param.objectId}">${param.value}</a>
    <script type="text/javascript">
        $("body").on("click", "#field_${field}_${param.instanceId}_${param.objectId}", function() {
            $.getJSON("${cp}jpm/${param.entityId}/${param.instanceId}/show.json?fields=${param.fields}", function(data) {
                var content = "<table class='table table-compact table-bordered'><tbody>";
                $.each(data, function(i, v) {
                    content = content + "<tr><th>" + i + "</th><td>" + v + "</td></tr>";
                });
                content = content + "</tbody></table><a href=\"javascript:$('" + "#field_${field}_${param.instanceId}_${param.objectId}" + "').popover('hide');\" class='pull-right'>close</a>";
                $("#field_${field}_${param.instanceId}_${param.objectId}").popover({placement: 'top', html: true, content: content}).popover('show');
            });
        });
    </script>
</c:if>