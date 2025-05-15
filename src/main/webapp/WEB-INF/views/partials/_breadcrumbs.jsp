<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %> <%-- Per c:url o spring:url --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%--
    Questo partial si aspetta una variabile nel model chiamata "breadcrumbs"
    che sia una List<com.myshop.ecommerce.model.BreadcrumbItem>
    Ogni BreadcrumbItem ha "label" e "url" (url può essere null per l'ultimo elemento)
--%>
<c:if test="${not empty breadcrumbs}">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-light"> <%-- Stile Bootstrap per breadcrumb --%>
            <c:forEach items="${breadcrumbs}" var="crumb" varStatus="loop">
                <c:choose>
                    <c:when test="${loop.last or empty crumb.url}"> <%-- Ultimo elemento o URL vuoto è attivo e non un link --%>
                        <li class="breadcrumb-item active" aria-current="page">
                            <c:out value="${crumb.label}"/>
                        </li>
                    </c:when>
                    <c:otherwise> <%-- Altri elementi sono link --%>
                        <li class="breadcrumb-item">
                            <a href="<c:url value='${crumb.url}'/>"><c:out value="${crumb.label}"/></a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ol>
    </nav>
</c:if>