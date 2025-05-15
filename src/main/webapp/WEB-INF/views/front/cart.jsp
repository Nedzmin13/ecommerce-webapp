<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Il Mio Carrello - MyShop</title>
    <!-- Bootstrap 4 CDN -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/custom.css'/>">

    <style>
        .cart-item-img {
            max-width: 80px;
            height: auto;
            margin-right: 15px;
        }
        .quantity-input {
            width: 70px;
            text-align: center;
        }
        .table th, .table td {
            vertical-align: middle;
        }
        .cart-summary {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-5">
    <h2>Il Mio Carrello</h2>

    <%-- Messaggi di Successo/Errore dal Controller --%>
    <c:if test="${not empty cartSuccessMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${cartSuccessMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">×</span>
            </button>
        </div>
    </c:if>
    <c:if test="${not empty cartErrorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${cartErrorMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">×</span>
            </button>
        </div>
    </c:if>

    <c:choose>
        <%-- Caso: Carrello Vuoto --%>
        <c:when test="${empty cart or empty cart.items}">
            <div class="alert alert-info text-center" role="alert">
                <i class="fas fa-shopping-cart fa-3x mb-3"></i>
                <h4 class="alert-heading">Il tuo carrello è vuoto!</h4>
                <p>Non hai ancora aggiunto prodotti al tuo carrello. Esplora il nostro catalogo!</p>
                <hr>
                <a href="<c:url value='/products'/>" class="btn btn-primary">Vai al Catalogo</a>
            </div>
        </c:when>

        <%-- Caso: Carrello con Articoli --%>
        <c:otherwise>
            <div class="row">
                    <%-- Lista Articoli nel Carrello --%>
                <div class="col-md-8">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th colspan="2">Prodotto</th>
                            <th class="text-center">Prezzo Unit.</th>
                            <th class="text-center">Quantità</th>
                            <th class="text-right">Subtotale</th>
                            <th></th> <%-- Per pulsante Rimuovi --%>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${cart.items}" var="item">
                            <tr>
                                <td>
                                    <c:url var="imageUrl" value="/images/${item.imageUrl != null ? item.imageUrl : 'placeholder.png'}"/>
                                    <img src="${imageUrl}" alt="${item.productName}" class="cart-item-img">
                                </td>
                                <td>
                                    <a href="<c:url value='/product/${item.productId}'/>">
                                        <c:out value="${item.productName}"/>
                                    </a>
                                </td>
                                <td class="text-center">
                                    <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="€ "/>
                                </td>
                                <td class="text-center">
                                        <%-- Form per aggiornare quantità --%>
                                    <form action="<c:url value='/cart/update'/>" method="post" class="form-inline justify-content-center">
                                        <input type="hidden" name="productId" value="${item.productId}">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control form-control-sm quantity-input"
                                               onchange="this.form.submit()" <%-- Invia form al cambio quantità --%>
                                               aria-label="Quantità prodotto ${item.productName}">
                                            <%--
                                            Potremmo aggiungere un pulsante di update esplicito invece di onchange:
                                            <button type="submit" class="btn btn-sm btn-outline-secondary ml-1" title="Aggiorna quantità">
                                                <i class="fas fa-sync-alt"></i>
                                            </button>
                                            --%>
                                    </form>
                                </td>
                                <td class="text-right">
                                    <strong><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="€ "/></strong>
                                </td>
                                <td class="text-center">
                                        <%-- Form per rimuovere item --%>
                                    <form action="<c:url value='/cart/remove/${item.productId}'/>" method="post">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Rimuovi prodotto">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                        <%-- Pulsante Svuota Carrello --%>
                    <form action="<c:url value='/cart/clear'/>" method="post" class="mt-3 text-right">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="btn btn-outline-danger" onclick="return confirm('Sei sicuro di voler svuotare il carrello?')">
                            <i class="fas fa-times-circle"></i> Svuota Carrello
                        </button>
                    </form>
                </div>

                    <%-- Riepilogo Carrello e Checkout --%>
                <div class="col-md-4">
                    <div class="cart-summary">
                        <h4>Riepilogo Ordine</h4>
                        <hr>
                        <p>
                            <strong>Articoli totali:</strong>
                            <span class="float-right"><c:out value="${cart.totalItems}"/></span>
                        </p>
                        <h5>
                            <strong>Importo Totale:</strong>
                            <span class="float-right">
                                    <fmt:formatNumber value="${cart.totalAmount}" type="currency" currencySymbol="€ "/>
                                </span>
                        </h5>
                        <hr>
                            <%--
                                Il pulsante Checkout dovrebbe essere abilitato solo se l'utente è loggato.
                                Se non è loggato, potrebbe reindirizzare al login.
                            --%>
                        <sec:authorize access="isAuthenticated()">
                            <a href="<c:url value='/checkout'/>" class="btn btn-success btn-block btn-lg mt-3">
                                <i class="fas fa-credit-card"></i> Procedi al Checkout
                            </a>
                        </sec:authorize>
                        <sec:authorize access="!isAuthenticated()">
                            <p class="text-center mt-3">
                                <a href="<c:url value='/login?redirect=/cart'/>" class="btn btn-info btn-block">
                                    <i class="fas fa-sign-in-alt"></i> Accedi per procedere
                                </a>
                            </p>
                            <p class="text-center small">oppure <a href="<c:url value='/register?redirect=/cart'/>">registrati</a></p>
                        </sec:authorize>

                        <a href="<c:url value='/products'/>" class="btn btn-outline-secondary btn-block mt-2">
                            <i class="fas fa-arrow-left"></i> Continua gli Acquisti
                        </a>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div> <%-- /container --%>

<jsp:include page="../partials/footer.jsp" /> <%-- Commenta se non esiste --%>

<!-- jQuery e Bootstrap Bundle -->
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>