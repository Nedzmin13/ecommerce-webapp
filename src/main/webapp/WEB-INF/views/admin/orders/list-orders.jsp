<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Gestione Ordini - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

</head>
<body>
<jsp:include page="../../partials/navbar.jsp" />

<div class="container mt-3"> <%-- Usa container o container-fluid a seconda del layout --%>
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container-fluid mt-4">
    <h2>Gestione Ordini</h2>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty orderPage && orderPage.hasContent()}">
            <table class="table table-striped table-bordered table-hover">
                <thead class="thead-dark">
                <tr>
                    <th>
                        <spring:url value="/admin/orders" var="sortOrderNumberUrl">
                            <spring:param name="page" value="${currentPage}"/> <spring:param name="size" value="${pageSize}"/>
                            <spring:param name="sortField" value="orderNumber"/> <spring:param name="sortDir" value="${sortField == 'orderNumber' ? reverseSortDir : 'asc'}"/>
                        </spring:url>
                        <a href="${sortOrderNumberUrl}">Num. Ordine</a>
                        <c:if test="${sortField == 'orderNumber'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'}"></i></c:if>
                    </th>
                    <th>
                        <spring:url value="/admin/orders" var="sortOrderDateUrl">
                            <spring:param name="page" value="${currentPage}"/> <spring:param name="size" value="${pageSize}"/>
                            <spring:param name="sortField" value="orderDate"/> <spring:param name="sortDir" value="${sortField == 'orderDate' ? reverseSortDir : 'asc'}"/>
                        </spring:url>
                        <a href="${sortOrderDateUrl}">Data</a>
                        <c:if test="${sortField == 'orderDate'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'}"></i></c:if>
                    </th>
                    <th>Cliente</th>
                    <th class="text-right">
                        <spring:url value="/admin/orders" var="sortTotalUrl">
                            <spring:param name="page" value="${currentPage}"/> <spring:param name="size" value="${pageSize}"/>
                            <spring:param name="sortField" value="totalAmount"/> <spring:param name="sortDir" value="${sortField == 'totalAmount' ? reverseSortDir : 'asc'}"/>
                        </spring:url>
                        <a href="${sortTotalUrl}">Totale</a>
                        <c:if test="${sortField == 'totalAmount'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'}"></i></c:if>
                    </th>
                    <th>
                        <spring:url value="/admin/orders" var="sortStatusUrl">
                            <spring:param name="page" value="${currentPage}"/> <spring:param name="size" value="${pageSize}"/>
                            <spring:param name="sortField" value="status"/> <spring:param name="sortDir" value="${sortField == 'status' ? reverseSortDir : 'asc'}"/>
                        </spring:url>
                        <a href="${sortStatusUrl}">Stato</a>
                        <c:if test="${sortField == 'status'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'}"></i></c:if>
                    </th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${orderPage.content}" var="order">
                    <tr>
                        <td><a href="<c:url value='/admin/orders/${order.id}'/>">#<c:out value="${order.orderNumber}"/></a></td>
                        <td>${order.orderDate.format(dateFormatter)}</td>
                        <td><c:out value="${order.user.firstName} ${order.user.lastName}"/> (<c:out value="${order.user.email}"/>)</td>
                        <td class="text-right"><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€ "/></td>
                        <td>
                                    <span class="badge badge-${order.status == 'DELIVERED' or order.status == 'COMPLETED' ? 'success' : (order.status == 'CANCELLED' or order.status == 'PAYMENT_FAILED' ? 'danger' : (order.status == 'SHIPPED' ? 'info' : (order.status == 'PROCESSING' ? 'primary' : 'warning')))}">
                                        <c:out value="${order.status}"/>
                                    </span>
                        </td>
                        <td>
                            <a href="<c:url value='/admin/orders/${order.id}'/>" class="btn btn-sm btn-info" title="Visualizza Dettagli">
                                <i class="fas fa-eye"></i>
                            </a>
                                <%-- Aggiungeremo pulsanti per modificare stato dopo --%>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <%-- Paginazione --%>
            <nav aria-label="Admin orders navigation">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${orderPage.isFirst() ? 'disabled' : ''}">
                        <spring:url value="/admin/orders" var="prevUrl"><spring:param name="page" value="${orderPage.number - 1}"/> <spring:param name="size" value="${pageSize}"/> <spring:param name="sortField" value="${sortField}"/> <spring:param name="sortDir" value="${sortDir}"/></spring:url>
                        <a class="page-link" href="${prevUrl}">«</a>
                    </li>
                    <c:forEach begin="0" end="${orderPage.totalPages - 1}" var="i">
                        <spring:url value="/admin/orders" var="pageUrl"><spring:param name="page" value="${i}"/> <spring:param name="size" value="${pageSize}"/> <spring:param name="sortField" value="${sortField}"/> <spring:param name="sortDir" value="${sortDir}"/></spring:url>
                        <li class="page-item ${orderPage.number == i ? 'active' : ''}">
                            <a class="page-link" href="${pageUrl}">${i + 1}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${orderPage.isLast() ? 'disabled' : ''}">
                        <spring:url value="/admin/orders" var="nextUrl"><spring:param name="page" value="${orderPage.number + 1}"/> <spring:param name="size" value="${pageSize}"/> <spring:param name="sortField" value="${sortField}"/> <spring:param name="sortDir" value="${sortDir}"/></spring:url>
                        <a class="page-link" href="${nextUrl}">»</a>
                    </li>
                </ul>
            </nav>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">Nessun ordine trovato nel sistema.</div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>