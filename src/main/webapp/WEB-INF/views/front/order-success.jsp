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
    <title>Ordine Completato - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-5 text-center">
    <div class="py-5">
        <i class="fas fa-check-circle fa-5x text-success mb-3"></i>
        <h2>Grazie per il tuo Ordine!</h2>
        <p class="lead">Il tuo ordine <strong>#<c:out value="${order.orderNumber}"/></strong> è stato ricevuto e verrà elaborato a breve.</p>
        <c:if test="${not empty transactionId}">
            <p>ID Transazione PayPal: <c:out value="${transactionId}"/></p>
        </c:if>
        <p>Riceverai un'email di conferma con i dettagli del tuo ordine.</p>
        <hr>
        <p>
            <a href="<c:url value='/products'/>" class="btn btn-primary mr-2">Continua gli Acquisti</a>
            <a href="<c:url value='/customer/orders'/>" class="btn btn-outline-secondary">Vai ai Miei Ordini</a>
        </p>
    </div>
</div>

<%-- <jsp:include page="../partials/footer.jsp" /> --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>