<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Registrazione - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">


    <style>
        .register-container {
            max-width: 550px;
            margin: 50px auto;
            padding: 30px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875em;
            display: block;
            margin-top: .25rem;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="register-container">
        <h2 class="text-center mb-4">Registrati su MyShop</h2>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty passwordError}">
            <div class="alert alert-danger">${passwordError}</div>
        </c:if>



        <form:form modelAttribute="userDto" action="${pageContext.request.contextPath}/register" method="post">

            <%-- Username --%>
            <div class="form-group">
                <form:label path="username">Username</form:label>
                <form:input path="username" cssClass="form-control" placeholder="Scegli un username" required="true" />
                <form:errors path="username" cssClass="error-message" />
            </div>

            <%-- Email --%>
            <div class="form-group">
                <form:label path="email">Email</form:label>
                <form:input path="email" type="email" cssClass="form-control" placeholder="iltuoindirizzo@example.com" required="true" />
                <form:errors path="email" cssClass="error-message" />
            </div>

            <div class="row">
                    <%-- Password --%>
                <div class="col-md-6 form-group">
                    <form:label path="password">Password</form:label>
                    <form:password path="password" cssClass="form-control" placeholder="Min. 8 caratteri" required="true" />
                    <form:errors path="password" cssClass="error-message" />
                </div>
                    <%-- Conferma Password --%>
                <div class="col-md-6 form-group">
                    <form:label path="confirmPassword">Conferma Password</form:label>
                    <form:password path="confirmPassword" cssClass="form-control" placeholder="Ripeti la password" required="true" />
                    <form:errors path="confirmPassword" cssClass="error-message" />
                </div>
            </div>

            <div class="row">
                    <%-- Nome --%>
                <div class="col-md-6 form-group">
                    <form:label path="firstName">Nome</form:label>
                    <form:input path="firstName" cssClass="form-control" required="true" />
                    <form:errors path="firstName" cssClass="error-message" />
                </div>
                    <%-- Cognome --%>
                <div class="col-md-6 form-group">
                    <form:label path="lastName">Cognome</form:label>
                    <form:input path="lastName" cssClass="form-control" required="true" />
                    <form:errors path="lastName" cssClass="error-message" />
                </div>
            </div>

            <%-- Pulsante Submit --%>
            <button type="submit" class="btn btn-primary btn-block mt-3">Registrati</button>

        </form:form>

        <hr>

        <%-- Link per Login --%>
        <div class="text-center mt-3">
            Hai già un account? <a href="<c:url value='/login'/>">Accedi</a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>