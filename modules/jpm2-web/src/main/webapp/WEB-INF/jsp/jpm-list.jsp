<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <spring:message var="entityName" code="${entity.title}" text="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header">
                    <h1>${operationName}</h1>
                    <div class="btn-group">
                        <s:iterator value="generalOperations" var="o">
                            <a class="btn" title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />">
                                <i class="glyphicon jpmicon-${o.id}"></i>
                            </a>
                        </s:iterator>
                    </div>
                </div>
                <div id="breadcrumb">
                    <a href="#" title="" class="tip-bottom current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
                    <a href="#" class="current">${operationName}</a>
                </div>
                <div class="container-fluid">
                    <div class="row"><br/>
                        <table class="table table-bordered table-compact table-striped">
                            <thead>
                                <tr>
                                    <th style="width: 5px;"><i class="glyphicon glyphicon-cog"></i></th>
                                        <s:iterator value="list.fields" var="field">
                                        <th><spring:message code="jpm.field.${entity.id}.${field.id}" text="${field.id}" /></th>
                                        </s:iterator>
                                </tr>
                            </thead>
                            <tbody>
                                <s:iterator value="list" var="item">
                                    <tr data-id="${item.id}">
                                        <td>
                                            <div class="btn-group nowrap">
                                                <s:iterator value="operations" var="o">
                                                    <a class="btn btn-mini btn-default" title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />">
                                                        <i class="glyphicon jpmicon-${o.id}"></i>
                                                    </a>
                                                </s:iterator>
                                            </div>
                                        </td>
                                        <s:iterator value="values" var="v">
                                            <td>${v.value}</td>
                                        </s:iterator>
                                    </tr>
                                </s:iterator>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>