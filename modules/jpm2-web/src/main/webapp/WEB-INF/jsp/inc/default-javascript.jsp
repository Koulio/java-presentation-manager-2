<c:if test="${pm.cssMode !='css'}">
    <script type="text/javascript" src="static/js/less-1.4.1.min.js?v=${pm.appversion}"></script>
</c:if>
<script type="text/javascript" src="static/js/jquery-2.0.3.min.js?v=${pm.appversion}" ></script>  
<script type="text/javascript" src="static/js/bootstrap.min.js?v=${pm.appversion}"></script>
<script type="text/javascript" src="static/js/jquery.jpanelmenu.min.js?v=${pm.appversion}"></script>
<script type="text/javascript">
    //This initialize the internationalized javascript messages
    var messages = new Array();
    messages["jpm.modal.confirm.title"] = "<spring:message code='jpm.modal.confirm.title' text='Confirm' />";
    messages["jpm.modal.confirm.cancel"] = "<spring:message code='jpm.modal.confirm.cancel' text='Cancel' />";
    messages["jpm.modal.confirm.submit"] = "<spring:message code='jpm.modal.confirm.submit' text='Ok' />";
    messages["jpm.modal.confirm.text"] = "<spring:message code='jpm.modal.confirm.text' text='Are you sure you want to continue?' />";
</script>
<script type="text/javascript" src="static/js/jpm.js?v=${pm.appversion}"></script>