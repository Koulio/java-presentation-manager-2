<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty param.len}">
    <input disabled="" class="form-control" type="text" value='<spring:message code="jpm.converter.showfile.bytes.text" text="File: ?" arguments="${param.len}" />' />
</c:if>
<c:if test="${empty param.len}">
    <input disabled="" class="form-control" type="text" value='<spring:message code="jpm.converter.file.null.file.text" text="-" />' />
</c:if>