<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %> <%-- Per accedere agli URL --%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> <%-- Tag di Spring Security --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Login - MyShop</title>
    <!-- Bootstrap 4 CDN -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">


    <!-- Custom CSS (opzionale) -->
    <%-- <link rel="stylesheet" href="<c:url value='/css/style.css'/>"> --%>
    <style>
        /* Stile minimale per centrare */
        .login-container {
            max-width: 400px;
            margin: 50px auto;
            padding: 30px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        .oauth-buttons a {
            margin-bottom: 10px;
        }
        .oauth-buttons img {
            height: 20px;
            margin-right: 8px;
            vertical-align: middle;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="login-container">
        <h2 class="text-center mb-4">Accedi a MyShop</h2>

        <%-- Messaggi di Errore/Successo Login --%>
        <c:if test="${param.error != null}">
            <div class="alert alert-danger" role="alert">
                Username o password non validi. Riprova.
            </div>
        </c:if>
        <c:if test="${param.logout != null}">
            <div class="alert alert-success" role="alert">
                Logout effettuato con successo.
            </div>
        </c:if>
        <c:if test="${param.registered != null}">
            <div class="alert alert-success" role="alert">
                Registrazione avvenuta con successo! Effettua il login.
            </div>
        </c:if>


        <%-- Form di Login Standard --%>
        <%-- L'action punta a /perform_login come configurato in SecurityConfig --%>
        <%-- Spring Security aggiungerà automaticamente il token CSRF se lo trova nella request --%>
        <form method="post" action="<c:url value='/perform_login'/>">
            <div class="form-group">
                <label for="username">Username o Email</label>
                <input type="text" class="form-control" id="username" name="username" required autofocus>
                <%-- name="username" deve corrispondere a usernameParameter in SecurityConfig --%>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" name="password" required>
                <%-- name="password" deve corrispondere a passwordParameter in SecurityConfig --%>
            </div>

            <%-- Input nascosto per il token CSRF --%>
            <%-- Nota: Spring Security lo aggiunge automaticamente se usi i tag <form:form> di Spring,
                 ma con un form HTML standard, dobbiamo aggiungerlo manualmente.
                 Il nome del parametro di default è _csrf --%>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <%-- Remember Me (Opzionale, da decommentare se abilitato in SecurityConfig) --%>
            <%--
            <div class="form-group form-check">
                <input type="checkbox" class="form-check-input" id="remember-me" name="remember-me">
                <label class="form-check-label" for="remember-me">Ricordami</label>
            </div>
            --%>

            <button type="submit" class="btn btn-primary btn-block">Login</button>
        </form>

        <hr>

        <%-- Link per la Registrazione --%>
        <div class="text-center mb-3">
            Non hai un account? <a href="<c:url value='/register'/>">Registrati ora</a>
        </div>

        <%-- Pulsanti Login Social (OAuth2) --%>
        <%-- Questi link puntano agli endpoint di autorizzazione standard di Spring Security OAuth2 Client --%>
        <%-- Il path base è /oauth2/authorization/{registrationId} --%>
        <div class="text-center oauth-buttons">
            <p>Oppure accedi con:</p>
            <a href="<c:url value='/oauth2/authorization/google'/>" class="btn btn-outline-danger btn-block">
                <%-- Potremmo usare icone FontAwesome o immagini --%>
                <%-- <img src="..." alt="Google logo"> --%>
                <i class="fab fa-google"></i> Accedi con Google
            </a>
            <a href="<c:url value='/oauth2/authorization/facebook'/>" class="btn btn-outline-primary btn-block">
                <%-- <img src="..." alt="Facebook logo"> --%>
                <i class="fab fa-facebook-f"></i> Accedi con Facebook
            </a>
            <%-- Nota: Per le icone (es. <i class="fab fa-google"></i>) è necessario includere FontAwesome --%>
            <%-- Aggiungi nel <head>: <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"> --%>
        </div>

    </div>
</div>

<!-- jQuery e Bootstrap Bundle (include Popper) via CDN -->
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-Fy6S3B9q64WdZWQUiU+q4/2Lc9npb8tCaSX9FK7E8HnRr0Jz8D6OP9dO5Vg3Q9ct" crossorigin="anonymous"></script>
<%-- Aggiungi Font Awesome per le icone social --%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</body>
</html>