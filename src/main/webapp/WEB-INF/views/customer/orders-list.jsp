<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>I Miei Ordini - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-3">
            <%-- Sidebar Menu Cliente --%>
            <h4>Area Cliente</h4>
            <div class="list-group">
                <a href="<c:url value='/customer/profile'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-user-circle"></i> Il Mio Profilo
                </a>
                <a href="<c:url value='/customer/orders'/>" class="list-group-item list-group-item-action active">
                    <i class="fas fa-history"></i> Cronologia Ordini
                </a>
            </div>
        </div>
        <div class="col-md-9">
            <h2>I Miei Ordini</h2>
            <hr>
            <c:choose>
                <c:when test="${not empty orderPage && orderPage.hasContent()}">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Numero Ordine</th>
                            <th>Data</th>
                            <th class="text-right">Totale</th>
                            <th>Stato</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${orderPage.content}" var="order">
                            <tr>
                                <td><a href="<c:url value='/customer/order/${order.id}'/>">#<c:out value="${order.orderNumber}"/></a></td>
                                <td>
                                    <c:if test="${not empty order.orderDate}">
                                        ${order.orderDate.format(dateFormatter)} <%-- Usa la variabile dal model --%>
                                    </c:if>
                                </td>
                                <td class="text-right"><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€ "/></td>
                                <td>
                                        <%-- Potremmo usare badge colorati per lo stato --%>
                                    <span class="badge badge-${order.status == 'DELIVERED' or order.status == 'COMPLETED' ? 'success' : (order.status == 'CANCELLED' or order.status == 'PAYMENT_FAILED' ? 'danger' : (order.status == 'SHIPPED' ? 'info' : 'warning'))}">
                                                <c:out value="${order.status}"/> <%-- TODO: Tradurre/Formattare meglio lo stato --%>
                                            </span>
                                </td>
                                <td>
                                    <a href="<c:url value='/customer/order/${order.id}'/>" class="btn btn-sm btn-info">Dettagli</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                    <%-- Paginazione --%>
                    <nav aria-label="Order history navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${orderPage.isFirst() ? 'disabled' : ''}">
                                <a class="page-link" href="<c:url value='/customer/orders?page=${orderPage.number - 1}'/>">«</a>
                            </li>
                            <c:forEach begin="0" end="${orderPage.totalPages - 1}" var="i">
                                <li class="page-item ${orderPage.number == i ? 'active' : ''}">
                                    <a class="page-link" href="<c:url value='/customer/orders?page=${i}'/>">${i + 1}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${orderPage.isLast() ? 'disabled' : ''}">
                                <a class="page-link" href="<c:url value='/customer/orders?page=${orderPage.number + 1}'/>">»</a>
                            </li>
                        </ul>
                    </nav>

                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">Non hai ancora effettuato ordini.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%-- <jsp:include page="../partials/footer.jsp" /> --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>