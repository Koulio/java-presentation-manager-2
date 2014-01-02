<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="${cp}static/css/login.css?v=${jpm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <jpm:jpm-body showHeader="false" showMenu="false">
        <div id="logo">
            <img src="<c:url value='/static/img/login.png'/>" alt="Login" />
        </div>
        <div id="loginbox">
            <form id="loginform" action="j_spring_security_check" method="POST">
                <h4><spring:message code="jpm.login.title" text="Enter username and password to continue." /></h4><hr/><br/>
                <div class="input-group">
                    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                    <input class="form-control" id="j_username" name="j_username" type="text" placeholder="<spring:message code="jpm.login.username" text="Username" />" />
                </div>
                <div class="input-group">
                    <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                    <input class="form-control" type="password" name="j_password" placeholder="<spring:message code="jpm.login.password" text="Password" />" />
                </div>
                <hr />
                <div class="form-actions">
                    <div class="pull-left">
                        <label>
                            <input type='checkbox' name='_spring_security_remember_me' /> <spring:message code="jpm.login.rememberme" text="Remember Me" />
                        </label>
                    </div>
                    <div class="pull-right">
                        <button type="submit" class="btn btn-default">
                            <spring:message code="jpm.login.submit" text="Login" />
                        </button>
                    </div>
                </div>
            </form>
        </div>
        <c:if test="${not empty error}">
            <br/>
            <div class="alert alert-danger pagination-centered">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
        </c:if>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#j_username").focus();
            });
        </script>
    </jpm:jpm-body>
</html>
