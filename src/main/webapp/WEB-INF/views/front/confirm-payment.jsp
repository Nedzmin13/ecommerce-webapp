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
    <title>Conferma Ordine #${order.orderNumber} - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">




    <script src="https://www.paypal.com/sdk/js?client-id=${paypalClientId}&currency=EUR&intent=capture"></script>
    <style>
        #paypal-button-container {
            max-width: 750px;
            margin: 20px auto;
        }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-5">
    <h2>Conferma il Tuo Ordine</h2>
    <p>Numero Ordine: <strong>#<c:out value="${order.orderNumber}"/></strong></p>
    <hr>

    <c:if test="${not empty checkoutMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${checkoutMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty paymentError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Errore Pagamento:</strong> ${paymentError}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>


    <div class="row">
        <div class="col-md-7">
            <h4>Dettagli Spedizione</h4>
            <address>
                <c:out value="${order.user.firstName}"/> <c:out value="${order.user.lastName}"/><br>
                <c:out value="${order.shipping.addressLine1}"/><br>
                <c:if test="${not empty order.shipping.addressLine2}"><c:out value="${order.shipping.addressLine2}"/><br></c:if>
                <c:out value="${order.shipping.postalCode}"/> <c:out value="${order.shipping.city}"/> (<c:out value="${order.shipping.state}"/>)<br>
                <c:out value="${order.shipping.country}"/><br>
                <c:if test="${not empty order.shipping.phone}">Tel: <c:out value="${order.shipping.phone}"/></c:if>
            </address>
            <a href="<c:url value='/checkout'/>" class="btn btn-sm btn-outline-secondary mb-3">Modifica Indirizzo</a>
        </div>
        <div class="col-md-5">
            <h4>Riepilogo Articoli</h4>
            <c:if test="${not empty order.orderItems}">
                <ul class="list-group mb-3">
                    <c:forEach items="${order.orderItems}" var="item">
                        <li class="list-group-item d-flex justify-content-between lh-condensed">
                            <div>
                                <h6 class="my-0"><c:out value="${item.product.name}"/> (x${item.quantity})</h6>
                                <small class="text-muted">Prezzo unitario: <fmt:formatNumber value="${item.pricePerUnit}" type="currency" currencySymbol="€ "/></small>
                            </div>
                            <span class="text-muted"><fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="€ "/></span>
                        </li>
                    </c:forEach>
                    <li class="list-group-item d-flex justify-content-between bg-light">
                            <span class="text-success">
                                 <h5 class="my-0">Totale Ordine</h5>
                            </span>
                        <strong class="text-success"><h5 class="my-0"><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€ "/></h5></strong>
                    </li>
                </ul>
            </c:if>
        </div>
    </div>

    <hr>
    <div class="text-center">
        <h4>Procedi con il Pagamento</h4>
        <p>Seleziona un metodo di pagamento per l'ordine <strong>#<c:out value="${order.orderNumber}"/></strong>.</p>

        <div id="paypal-button-container">
        </div>
        <div id="paypal-message-container" class="mt-2"></div> <%-- Per messaggi di errore/successo da PayPal --%>


        <p class="mt-3"><small>Cliccando su un pulsante di pagamento, accetti i nostri termini e condizioni.</small></p>
        <form action="<c:url value='/checkout/cancel-order/${order.id}'/>" method="post" class="mt-2">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Sei sicuro di voler annullare questo ordine? Dovrai ricominciare il checkout.')">Annulla Ordine</button>
        </form>
    </div>
</div>



<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/js/all.min.js"></script>

<script>
    const orderTotalAmountString = "${order.totalAmount}";
    const orderIdInternal = "${order.id}";
    const csrfToken = "${_csrf.token}";
    const csrfHeaderName = "${_csrf.headerName}";

    const orderTotalAmount = parseFloat(orderTotalAmountString.replace(',', '.'));

    const paypalMessageContainer = document.getElementById('paypal-message-container');

    function showPayPalMessage(message, isError = false) {
        paypalMessageContainer.innerHTML = `<div class="alert alert-${isError ? 'danger' : 'info'}">${message}</div>`;
    }

    if (typeof paypal !== 'undefined' && !isNaN(orderTotalAmount) && orderTotalAmount > 0 && orderIdInternal) {
        paypal.Buttons({
            // STILE DEI PULSANTI (opzionale)
            style: {
                layout: 'vertical',
                color:  'gold',
                shape:  'rect',
                label:  'paypal',
                tagline: false
            },

            createOrder: function(data, actions) {
                console.log("PayPal: createOrder function triggered.");
                showPayPalMessage("Inizializzazione pagamento con PayPal...");
                return fetch('<c:url value="/api/paypal/create-order"/>/' + orderIdInternal, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        [csrfHeaderName]: csrfToken
                    }
                }).then(function(res) {
                    if (!res.ok) {
                        return res.json().then(errData => {
                            console.error("PayPal: Errore server durante createOrder:", errData);
                            throw new Error(errData.message || `Server error: ${res.status}`);
                        });
                    }
                    return res.json();
                }).then(function(orderData) {
                    if (!orderData || !orderData.id) {
                        console.error("PayPal: Risposta non valida da create-order:", orderData);
                        throw new Error("ID ordine PayPal non ricevuto dal server.");
                    }
                    console.log("PayPal: Ordine PayPal creato con ID:", orderData.id);
                    showPayPalMessage("Pronto per il pagamento...");
                    return orderData.id;
                }).catch(function(err) {
                    console.error('PayPal: Errore in createOrder:', err);
                    showPayPalMessage('Errore durante la preparazione del pagamento: ' + err.message, true);
                });
            },

            onApprove: function(data, actions) {
                console.log("PayPal: onApprove function triggered. Data:", data);
                showPayPalMessage("Processazione pagamento in corso...");
                return fetch('<c:url value="/api/paypal/capture-order"/>/' + data.orderID, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        [csrfHeaderName]: csrfToken
                    }
                }).then(function(res) {
                    if (!res.ok) {
                        return res.json().then(errData => {
                            console.error("PayPal: Errore server durante captureOrder:", errData);
                            throw new Error(errData.message || `Server error: ${res.status}`);
                        });
                    }
                    return res.json();
                }).then(function(captureData) {
                    console.log('PayPal: Pagamento catturato:', captureData);

                    var internalOrderIdForRedirect = captureData.internalOrderId || orderIdInternal;

                    if (captureData.status === 'COMPLETED' || (captureData.details && captureData.details.status === 'COMPLETED') ) {
                        showPayPalMessage("Pagamento completato con successo!", false);
                        window.location.href = '<c:url value="/order/success"/>?orderId=' + internalOrderIdForRedirect + '&transactionId=' + (captureData.transactionId || data.orderID);
                    } else {
                        console.warn("PayPal: Stato pagamento non COMPLETED:", captureData);
                        showPayPalMessage("Il pagamento è stato processato ma lo stato non è completo. Contatta l'assistenza.", true);
                        window.location.href = '<c:url value="/checkout/payment-pending"/>?orderId=' + internalOrderIdForRedirect;
                    }
                }).catch(function(err) {
                    console.error('PayPal: Errore in onApprove (cattura):', err);
                    showPayPalMessage('Errore durante la finalizzazione del pagamento: ' + err.message, true);
                    window.location.href = '<c:url value="/checkout/payment-error"/>?orderId=' + orderIdInternal + '&message=' + encodeURIComponent(err.message);
                });
            },

            onCancel: function(data) {
                console.log("PayPal: onCancel function triggered. Data:", data);
                showPayPalMessage("Pagamento annullato dall'utente.");

                // window.location.href = '<c:url value="/checkout/confirm"/>/' + orderIdInternal + '?status=cancelled';
            },

            onError: function(err) {
                console.error("PayPal: onError SDK function triggered:", err);
                showPayPalMessage('Si è verificato un errore con PayPal: ' + err.toString(), true);

            }
        }).render('#paypal-button-container');
    } else {
        console.warn("PayPal SDK non caricato, o dati ordine (totale/ID) mancanti/invalidi.");
        document.getElementById('paypal-button-container').innerHTML = '<p class="text-danger">Impossibile caricare le opzioni di pagamento PayPal in questo momento. Verifica che il totale dell\'ordine sia valido.</p>';
    }
</script>
</body>
</html>