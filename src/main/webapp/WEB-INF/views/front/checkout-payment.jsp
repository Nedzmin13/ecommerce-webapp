<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Conferma e Pagamento - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">


</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-5">
    <h2>Conferma Ordine e Paga</h2>
    <hr>

    <c:if test="${not empty checkoutMessage}">
        <div class="alert alert-info">${checkoutMessage}</div>
    </c:if>

    <div class="row">
        <div class="col-md-7">
            <h4>Rivedi il tuo Ordine</h4>
            <p><strong>Indirizzo di Spedizione:</strong></p>
            <address>
                <c:out value="${shippingAddress.addressLine1}"/><br>
                <c:if test="${not empty shippingAddress.addressLine2}"><c:out value="${shippingAddress.addressLine2}"/><br></c:if>
                <c:out value="${shippingAddress.postalCode}"/> <c:out value="${shippingAddress.city}"/> (<c:out value="${shippingAddress.state}"/>)<br>
                <c:out value="${shippingAddress.country}"/><br>
                <c:if test="${not empty shippingAddress.phone}">Tel: <c:out value="${shippingAddress.phone}"/></c:if>
            </address>
            <a href="<c:url value='/checkout'/>" class="btn btn-sm btn-outline-secondary mb-3">Modifica Indirizzo</a>
        </div>
        <div class="col-md-5">
            <h4>Riepilogo Carrello</h4>
            <c:if test="${not empty cart && not empty cart.items}">
                <ul class="list-group mb-3">
                    <c:forEach items="${cart.items}" var="item">
                        <li class="list-group-item d-flex justify-content-between lh-condensed">
                            <div>
                                <h6 class="my-0"><c:out value="${item.productName}"/> (x${item.quantity})</h6>
                            </div>
                            <span class="text-muted"><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="€ "/></span>
                        </li>
                    </c:forEach>
                    <li class="list-group-item d-flex justify-content-between">
                        <span>Totale (EUR)</span>
                        <strong><fmt:formatNumber value="${cart.totalAmount}" type="currency" currencySymbol="€ "/></strong>
                    </li>
                </ul>
            </c:if>
        </div>
    </div>

    <hr>
    <div class="text-center">
        <h4>Procedi con il Pagamento</h4>
        <p>Verrai reindirizzato a PayPal per completare il pagamento.</p>
        <%-- Qui integreremo il pulsante PayPal --%>
        <div id="paypal-button-container" style="max-width: 400px; margin: 20px auto;">
            <!-- PayPal Button Will Be Rendered Here -->
            <p class="text-muted"><em>(Integrazione PayPal in arrivo)</em></p>
            <a href="#" class="btn btn-warning btn-lg disabled">Paga con PayPal (Disabilitato)</a>
        </div>
        <p><small>Cliccando su "Paga con PayPal", accetti i nostri termini e condizioni.</small></p>
    </div>

</div>

<jsp:include page="../partials/footer.jsp" /> <%-- Commenta se non esiste --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>