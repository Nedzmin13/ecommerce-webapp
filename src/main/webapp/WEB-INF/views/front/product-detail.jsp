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
    <title><c:out value="${product.name}"/> - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        .product-detail-image {
            max-width: 100%;
            max-height: 450px;
            object-fit: contain;
            border: 1px solid #eee;
            padding: 15px;
            border-radius: .25rem;
            margin-bottom: 20px;
        }
        .quantity-input {
            width: 80px;
            text-align: center;
        }
        .price {
            font-size: 2rem;
            color: #28a745;
        }
        /* Stile per i breadcrumb (già in _breadcrumbs.jsp, ma possiamo sovrascrivere o aggiungere) */
        /* .breadcrumb { background-color: #f8f9fa; } */
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<%-- INCLUDI BREADCRUMB QUI --%>
<div class="container mt-3">
    <jsp:include page="../partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-4"> <%-- Contenitore principale leggermente spostato --%>
    <c:choose>
        <c:when test="${not empty product}"> <%-- Verifica se product non è nullo --%>
            <div class="row">
                <!-- Immagine Prodotto -->
                <div class="col-md-6 text-center">
                    <c:url var="detailProductImageUrl" value="/images/products/${product.imageUrl != null && not empty product.imageUrl ? product.imageUrl : 'placeholder.png'}"/>
                    <img src="${detailProductImageUrl}" alt="<c:out value="${product.name}"/>" class="product-detail-image">
                </div>
                <!-- Dettagli Prodotto e Azioni -->
                <div class="col-md-6">
                    <h1><c:out value="${product.name}"/></h1>
                    <h4 class="text-muted">
                        <a href="<c:url value='/products?category=${product.category.id}'/>" class="text-muted">
                            <c:out value="${product.category.name}"/>
                        </a>
                    </h4>
                    <p class="price my-3">
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€ " />
                    </p>
                    <p class="text-${product.available && product.stockQuantity > 0 ? 'success' : 'danger'}">
                        <c:choose>
                            <c:when test="${product.available && product.stockQuantity > 0}">
                                <i class="fas fa-check-circle"></i> Disponibile
                                <span class="text-muted small">(Quantità: <c:out value="${product.stockQuantity}"/>)</span>
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-times-circle"></i> Non disponibile
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <hr>
                    <form action="<c:url value='/cart/add'/>" method="post" class="mt-3">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <div class="form-row align-items-center">
                            <div class="col-auto">
                                <label for="quantityDetail" class="mr-2">Quantità:</label>
                                <input type="number" class="form-control quantity-input" id="quantityDetail" name="quantity" value="1" min="1" max="${product.stockQuantity > 0 ? product.stockQuantity : 1}" ${!product.available || product.stockQuantity <= 0 ? 'disabled' : ''} required>
                            </div>
                            <div class="col-auto">
                                <button type="submit" class="btn btn-primary btn-lg" ${!product.available || product.stockQuantity <= 0 ? 'disabled' : ''}>
                                    <i class="fas fa-shopping-cart"></i> Aggiungi al Carrello
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div class="row mt-5">
                <div class="col-12">
                    <h4>Descrizione Prodotto</h4>
                    <div class="description-content bg-light p-3 rounded">
                        <p style="white-space: pre-wrap;"><c:out value="${product.description}" escapeXml="true"/></p>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger text-center mt-4" role="alert">
                <h4><i class="fas fa-exclamation-triangle"></i> Prodotto Non Trovato</h4>
                <p>Il prodotto che stai cercando non esiste o non è più disponibile.</p>
                <a href="<c:url value='/products'/>" class="btn btn-primary mt-2">Torna al Catalogo</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%-- <jsp:include page="../partials/footer.jsp" /> --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>