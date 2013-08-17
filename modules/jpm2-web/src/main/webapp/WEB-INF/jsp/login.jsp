<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
        <link href="static/css/login.css?v=${pm.appversion}" rel="stylesheet" type="text/css" />
    </head>
    <body  onload='document.loginform.j_username.focus();'>
        <div id="container" class="container">
            <div id="logo">
                <img src="static/img/login.png" alt="Login" />
            </div>
            <div id="loginbox">            
                <form id="loginform" action="j_spring_security_check" method="POST">
                    <h4><spring:message code="jpm.login.title" text="Enter username and password to continue." /></h4><hr/><br/>
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                        <input class="form-control" name="j_username" type="text" placeholder="<spring:message code="jpm.login.username" text="Username" />" />
                    </div>
                    <div class="input-group">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                        <input class="form-control" type="password" name="j_password" placeholder="<spring:message code="jpm.login.password" text="Password" />" />
                    </div>
                    <hr />
                    <div class="form-actions">
                        <div class="pull-left">
                            <input type='checkbox' name='_spring_security_remember_me'/> <spring:message code="jpm.login.rememberme" text="Remember Me" />
                        </div>
                        <div class="pull-right"><input type="submit" class="btn btn-default" value="Login" /></div>
                    </div>
                </form>
            </div>
            <c:if test="${not empty error}">
                <br/>
                <div class="alert alert-danger pagination-centered">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
            </c:if>
            <%@include  file="inc/footer.jsp" %>
        </div>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type="text/javascript">
        $(document).ready(function() {
            if ($.browser.msie === true && $.browser.version.slice(0, 3) < 10) {
                $('input[placeholder]').each(function() {
                    var input = $(this);
                    $(input).val(input.attr('placeholder'));
                    $(input).focus(function() {
                        if (input.val() === input.attr('placeholder')) {
                            input.val('');
                        }
                    });
                    $(input).blur(function() {
                        if (input.val() === '' || input.val() === input.attr('placeholder')) {
                            input.val(input.attr('placeholder'));
                        }
                    });
                });
            }
        });
        </script> 
    </body>
</html>
