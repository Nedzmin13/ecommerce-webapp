<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Pagina Non Trovata (404) - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">


    <style>
        body { display: flex; min-height: 100vh; flex-direction: column; }
        main { flex: 1; }
        .error-template {padding: 40px 15px;text-align: center;}
        .error-actions {margin-top:15px;margin-bottom:15px;}
        .error-actions .btn { margin-right:10px; }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<main class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>Oops!</h1>
                <h2>Errore 404 - Pagina Non Trovata</h2>
                <div class="error-details">
                    Spiacenti, la pagina che stai cercando non esiste o è stata spostata.
                    <c:if test="${not empty errorMessage}"> <%-- Mostra messaggio specifico se passato --%>
                        <br/><em>Dettaglio: ${errorMessage}</em>
                    </c:if>
                    <c:if test="${not empty exception}"> <%-- In DEV potresti mostrare dettagli eccezione (con cautela) --%>
                        <%-- <br/><pre>${exception.message}</pre> --%>
                    </c:if>
                </div>
                <div class="error-actions">
                    <a href="<c:url value='/'/>" class="btn btn-primary btn-lg">
                        <span class="fas fa-home"></span>
                        Torna alla Home
                    </a>
                    <a href="<c:url value='/products'/>" class="btn btn-outline-secondary btn-lg">
                        <span class="fas fa-shopping-bag"></span>
                        Vai al Catalogo
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<%-- Footer opzionale --%>
<%-- <jsp:include page="../partials/footer.jsp" /> --%>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/js/all.min.js"></script> <%-- Per icone FontAwesome --%>
</body>
</html>