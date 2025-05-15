<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Checkout - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        .summary-table th, .summary-table td { padding: 0.5rem; }
        .error-message { color: #dc3545; font-size: 0.875em; display: block; margin-top: .25rem;}
        .checkout-summary { background-color: #f8f9fa; padding: 20px; border-radius: 5px; }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-5">
    <h2>Checkout</h2>
    <hr>

    <%-- Messaggi Flash --%>
    <c:if test="${not empty checkoutMessage}">
        <div class="alert alert-info">${checkoutMessage}</div>
    </c:if>

    <div class="row">
        <%-- Riepilogo Carrello (Colonna Sinistra/Superiore) --%>
        <div class="col-lg-5 order-lg-2 mb-4">
            <div class="checkout-summary">
                <h4>Riepilogo Ordine</h4>
                <c:if test="${not empty cart && not empty cart.items}">
                    <table class="table table-sm summary-table">
                        <tbody>
                        <c:forEach items="${cart.items}" var="item">
                            <tr>
                                <td><c:out value="${item.productName}"/> (x<c:out value="${item.quantity}"/>)</td>
                                <td class="text-right">
                                    <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="€ "/>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr class="font-weight-bold">
                            <td>Totale Provvisorio</td>
                            <td class="text-right">
                                <fmt:formatNumber value="${cart.totalAmount}" type="currency" currencySymbol="€ "/>
                            </td>
                        </tr>
                            <%-- Aggiungere costi spedizione e tasse in futuro --%>
                        <tr class="font-weight-bold h5">
                            <td>Totale da Pagare</td>
                            <td class="text-right">
                                <fmt:formatNumber value="${cart.totalAmount}" type="currency" currencySymbol="€ "/>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>

        <%-- Form Indirizzo Spedizione (Colonna Destra/Inferiore) --%>
        <div class="col-lg-7 order-lg-1">
            <h4>Indirizzo di Spedizione</h4>
            <form:form modelAttribute="shippingAddressDto" action="${pageContext.request.contextPath}/checkout/submit-address" method="post">
                <%-- addressLine1 --%>
                <div class="form-group">
                    <form:label path="addressLine1">Indirizzo (Via, Piazza, Numero Civico)</form:label>
                    <form:input path="addressLine1" cssClass="form-control" placeholder="Es. Via Roma, 123" required="true"/>
                    <form:errors path="addressLine1" cssClass="error-message"/>
                </div>

                <%-- addressLine2 (Opzionale) --%>
                <div class="form-group">
                    <form:label path="addressLine2">Indirizzo Linea 2 (Interno, Scala, C/O)</form:label>
                    <form:input path="addressLine2" cssClass="form-control" placeholder="Es. Interno 5, Scala B"/>
                    <form:errors path="addressLine2" cssClass="error-message"/>
                </div>

                <div class="row">
                        <%-- city --%>
                    <div class="col-md-6 form-group">
                        <form:label path="city">Città</form:label>
                        <form:input path="city" cssClass="form-control" required="true"/>
                        <form:errors path="city" cssClass="error-message"/>
                    </div>
                        <%-- postalCode --%>
                    <div class="col-md-6 form-group">
                        <form:label path="postalCode">CAP (Codice Avviamento Postale)</form:label>
                        <form:input path="postalCode" cssClass="form-control" required="true"/>
                        <form:errors path="postalCode" cssClass="error-message"/>
                    </div>
                </div>

                <div class="row">
                        <%-- state (Provincia/Regione) --%>
                    <div class="col-md-6 form-group">
                        <form:label path="state">Provincia / Regione</form:label>
                        <form:input path="state" cssClass="form-control" placeholder="Es. RM o Lazio" required="true"/>
                        <form:errors path="state" cssClass="error-message"/>
                    </div>
                        <%-- country --%>
                    <div class="col-md-6 form-group">
                        <form:label path="country">Paese</form:label>
                        <form:input path="country" cssClass="form-control" value="Italia" required="true"/> <%-- Default Italia --%>
                        <form:errors path="country" cssClass="error-message"/>
                    </div>
                </div>

                <%-- phone (Opzionale) --%>
                <div class="form-group">
                    <form:label path="phone">Numero di Telefono (Opzionale)</form:label>
                    <form:input path="phone" type="tel" cssClass="form-control" placeholder="Per eventuali comunicazioni sulla consegna"/>
                    <form:errors path="phone" cssClass="error-message"/>
                </div>

                <hr class="mb-4">
                <button class="btn btn-primary btn-lg btn-block" type="submit">
                    <i class="fas fa-arrow-right"></i> Salva Indirizzo e Continua
                </button>
            </form:form>
        </div>
    </div>
</div>

<jsp:include page="../partials/footer.jsp" /> <%-- Commenta se non esiste --%>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>