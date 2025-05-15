<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>MyShop E-commerce</title>
    <!-- Bootstrap 4 CDN -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <a class="navbar-brand" href="<c:url value='/'/>">MyShop</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item active">
                <a class="nav-link" href="<c:url value='/'/>">Home <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<c:url value='/products'/>">Prodotti</a> <%-- Link al catalogo --%>
            </li>
            <%-- Link Admin visibile solo a ROLE_ADMIN --%>
            <sec:authorize access="hasRole('ADMIN')">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/admin/dashboard'/>">Admin Dashboard</a>
                </li>
            </sec:authorize>
            <%-- Link Area Cliente visibile solo se loggati --%>
            <sec:authorize access="isAuthenticated() and !hasRole('ADMIN')"> <%-- Mostra solo a CUSTOMER --%>
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/customer/profile'/>">Area Cliente</a>
                </li>
            </sec:authorize>

        </ul>
        <ul class="navbar-nav">
            <%-- Se l'utente NON è autenticato, mostra Login/Registrati --%>
            <sec:authorize access="!isAuthenticated()">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/login'/>">Login</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link btn btn-outline-primary btn-sm" href="<c:url value='/register'/>">Registrati</a>
                </li>
            </sec:authorize>

            <%-- Se l'utente È autenticato, mostra nome utente e Logout --%>
            <sec:authorize access="isAuthenticated()">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <sec:authentication property="principal.username" /> <%-- Mostra username --%>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdown">
                        <sec:authorize access="hasRole('CUSTOMER')">
                            <a class="dropdown-item" href="<c:url value='/customer/profile'/>">Il mio Profilo</a>
                            <a class="dropdown-item" href="<c:url value='/customer/orders'/>">I miei Ordini</a>
                            <div class="dropdown-divider"></div>
                        </sec:authorize>
                        <sec:authorize access="hasRole('ADMIN')">
                            <a class="dropdown-item" href="<c:url value='/admin/dashboard'/>">Dashboard Admin</a>
                            <div class="dropdown-divider"></div>
                        </sec:authorize>

                            <%-- Logout Form (POST è più sicuro per logout) --%>
                        <form id="logoutForm" method="post" action="<c:url value='/logout'/>" style="display: none;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        </form>
                        <a class="dropdown-item" href="#" onclick="document.getElementById('logoutForm').submit();">Logout</a>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Carrello (0)</a> <%-- Placeholder Carrello --%>
                </li>
            </sec:authorize>
        </ul>
    </div>
</nav>

<div class="container">
    <h1>Benvenuto su MyShop!</h1>
    <p>Il tuo negozio online di fiducia per l'elettronica.</p>

    <%-- Messaggio di successo login --%>
    <c:if test="${param.loginSuccess != null}">
        <div class="alert alert-success" role="alert">
            Login effettuato con successo!
        </div>
    </c:if>

    <p><a href="<c:url value='/products'/>" class="btn btn-primary">Vai al Catalogo Prodotti</a></p>

</div>

<!-- jQuery e Bootstrap Bundle (include Popper) via CDN -->
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>