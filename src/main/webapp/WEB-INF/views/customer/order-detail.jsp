<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Per i numeri --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Dettaglio Ordine #${order.orderNumber} - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-5">
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
        <p><a href="<c:url value='/customer/orders'/>" class="btn btn-primary">Torna alla Cronologia Ordini</a></p>
    </c:if>

    <c:if test="${not empty order}">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2>Dettaglio Ordine #<c:out value="${order.orderNumber}"/></h2>
            <a href="<c:url value='/customer/orders'/>" class="btn btn-outline-secondary"><i class="fas fa-arrow-left"></i> Torna ai Miei Ordini</a>
        </div>
        <hr>

        <div class="row">
            <div class="col-md-6">
                <h4>Informazioni Ordine</h4>
                <p><strong>Data:</strong>
                    <c:if test="${not empty order.orderDate}">
                        ${order.orderDate.format(dateFormatter)}
                    </c:if>
                </p>
                <p><strong>Stato:</strong> <span class="badge badge-primary"><c:out value="${order.status}"/></span></p>
                <p><strong>Totale:</strong> <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€ "/></p>
            </div>
            <div class="col-md-6">
                <h4>Indirizzo di Spedizione</h4>
                <address>
                    <strong><c:out value="${order.user.firstName}"/> <c:out value="${order.user.lastName}"/></strong><br>
                    <c:out value="${order.shipping.addressLine1}"/><br>
                    <c:if test="${not empty order.shipping.addressLine2}"><c:out value="${order.shipping.addressLine2}"/><br></c:if>
                    <c:out value="${order.shipping.postalCode}"/> <c:out value="${order.shipping.city}"/> (<c:out value="${order.shipping.state}"/>)<br>
                    <c:out value="${order.shipping.country}"/><br>
                    <c:if test="${not empty order.shipping.phone}">Tel: <c:out value="${order.shipping.phone}"/></c:if>
                </address>
            </div>
        </div>

        <h4 class="mt-4">Articoli Ordinati</h4>
        <table class="table table-bordered">
            <thead>
            <tr>
                <th>Prodotto</th>
                <th class="text-center">Quantità</th>
                <th class="text-right">Prezzo Unit.</th>
                <th class="text-right">Subtotale</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${order.orderItems}" var="item">
                <tr>
                    <td><c:out value="${item.product.name}"/></td>
                    <td class="text-center"><c:out value="${item.quantity}"/></td>
                    <td class="text-right"><fmt:formatNumber value="${item.pricePerUnit}" type="currency" currencySymbol="€ "/></td>
                    <td class="text-right"><fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="€ "/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${not empty order.payment}">
            <h4 class="mt-4">Dettagli Pagamento</h4>
            <p><strong>Metodo:</strong> <c:out value="${order.payment.paymentMethod}"/></p>
            <p><strong>ID Transazione:</strong> <c:out value="${order.payment.transactionId}"/></p>
            <p><strong>Data Pagamento:</strong>
                <c:if test="${not empty order.payment.paymentDate}">
                    ${order.payment.paymentDate.format(dateFormatter)}
                </c:if>
                <c:if test="${empty order.payment.paymentDate}">N/D</c:if>
            </p>
            <p><strong>Stato Pagamento:</strong> <c:out value="${order.payment.status}"/></p>
        </c:if>
    </c:if>
</div>

<%-- <jsp:include page="../partials/footer.jsp" /> --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>