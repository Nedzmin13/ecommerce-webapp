<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:if test="${not empty breadcrumbs}">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-light">
            <c:forEach items="${breadcrumbs}" var="crumb" varStatus="loop">
                <c:choose>
                    <c:when test="${loop.last or empty crumb.url}">
                        <li class="breadcrumb-item active" aria-current="page">
                            <c:out value="${crumb.label}"/>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="breadcrumb-item">
                            <a href="<c:url value='${crumb.url}'/>"><c:out value="${crumb.label}"/></a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </ol>
    </nav>
</c:if>