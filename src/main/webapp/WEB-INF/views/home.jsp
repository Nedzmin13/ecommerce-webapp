<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Per formattare i prezzi --%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Per le funzioni JSTL --%>


<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>MyShop E-commerce - Elettronica di Qualità</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <%-- Google Fonts --%>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">
    <%-- Custom CSS --%>
    <link rel="stylesheet" href="<c:url value='/css/custom.css'/>">

    <style>
        /* Stili specifici per la homepage, potrebbero andare in custom.css */
        body {
            font-family: 'Open Sans', sans-serif;
            color: #495057;
            background-color: #f8f9fa; /* Sfondo leggermente grigio */
        }

        h1, h2, h3, h4, h5, h6 {
            font-family: 'Montserrat', sans-serif;
            font-weight: 500; /* Medio per i titoli */
        }

        .hero-section {
            background-image: linear-gradient(rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.4)), url('<c:url value="/images/homepage/hero-banner.jpg"/>');
            background-size: cover;
            background-position: center center;
            color: white;
            padding: 10rem 1rem; /* Aumentato padding */
            text-align: center;
            margin-bottom: 3rem;
        }
        .hero-section h1 {
            font-size: 3.8rem;
            font-weight: 700;
            text-shadow: 2px 2px 6px rgba(0,0,0,0.6);
            margin-bottom: 1rem;
        }
        .hero-section p.lead {
            font-size: 1.35rem;
            margin-bottom: 2.5rem;
            text-shadow: 1px 1px 3px rgba(0,0,0,0.5);
            font-weight: 400;
        }
        .hero-section .btn-lg {
            font-size: 1.1rem;
            padding: 0.8rem 2.2rem;
            font-weight: 600;
        }

        .section-title {
            text-align: center;
            margin-bottom: 3rem;
            font-size: 2.2rem;
            font-weight: 500;
            color: #343a40;
        }
        .section-title::after {
            content: ''; display: block; width: 70px; height: 3px;
            background-color: #007bff; margin: 10px auto 0;
        }

        .category-card {
            text-align: center;
            border: 1px solid #e0e0e0;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            margin-bottom: 1.5rem;
            background-color: #fff;
            border-radius: .5rem;
            overflow: hidden; /* Per contenere l'immagine */
        }
        .category-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 .5rem 1.5rem rgba(0,0,0,.15)!important;
        }
        .category-card img {
            width: 100%;
            height: 160px;
            object-fit: cover;
        }
        .category-card .card-body { padding: 1.25rem; }
        .category-card .card-title { font-size: 1.15rem; font-weight: 500; color: #333; margin-bottom:0.75rem; }

        .product-card-home { /* Stile per le card prodotto nella homepage */
            margin-bottom: 30px;
            height: 100%;
            display: flex;
            flex-direction: column;
            border: 1px solid #e0e0e0;
            border-radius: .35rem;
            transition: box-shadow .3s;
        }
        .product-card-home:hover {
            box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
        }
        .product-card-home .product-card-img-top {
            max-height: 170px;
            width: 100%;
            object-fit: contain;
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        .product-card-home .card-body { padding: 1rem; flex-grow:1; display:flex; flex-direction:column;}
        .product-card-home .card-title { font-size: 1rem; font-weight: 500; min-height: 40px; margin-bottom:0.5rem;}
        .product-card-home .card-text.price { font-size: 1.15rem; font-weight: 600; color: #28a745; margin-bottom:0.75rem;}
        .product-card-home .card-footer { background-color: transparent; border-top: none; padding-top:0; margin-top:auto; }


        .content-section {
            padding: 2.5rem 0;
        }
        .bg-light-custom {
            background-color: #f8f9fa!important; /* Sfondo leggermente grigio */
        }
    </style>
</head>
<body>
<jsp:include page="partials/navbar.jsp" />

<%-- Hero Section --%>
<header class="hero-section">
    <div class="container">
        <h1>MyShop: Tecnologia all'Avanguardia</h1>
        <p class="lead">Esplora le ultime innovazioni e trova il dispositivo perfetto per te.</p>
        <a href="<c:url value='/products'/>" class="btn btn-primary btn-lg">Scopri i Prodotti</a>
    </div>
</header>

<div class="container">
    <%-- Messaggio di Successo Login (se presente) --%>
    <c:if test="${param.loginSuccess != null || param.oauthLoginSuccess != null}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle mr-2"></i> Login effettuato con successo! Bentornato.
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <%-- Sezione Categorie in Evidenza --%>
    <section class="content-section" id="categories">
        <h2 class="section-title">Le Nostre Categorie</h2>
        <div class="row">
            <c:forEach items="${featuredCategories}" var="category">
                <div class="col-sm-6 col-md-3 mb-3">
                    <div class="card category-card">
                        <c:url var="categoryImageUrl" value="/images/homepage/category-${fn:toLowerCase(fn:replace(category.name, ' ', '-'))}.jpg"/>
                        <a href="<c:url value='/products?category=${category.id}'/>">
                            <img src="${categoryImageUrl}" class="card-img-top" alt="<c:out value='${category.name}'/>">
                        </a>
                        <div class="card-body">
                            <h5 class="card-title"><c:out value="${category.name}"/></h5>
                            <a href="<c:url value='/products?category=${category.id}'/>" class="btn btn-outline-primary btn-sm">Esplora</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <%-- Sezione Nuovi Arrivi --%>
    <c:if test="${not empty latestProducts}">
        <section class="content-section bg-light-custom" id="latest-products">
            <h2 class="section-title">Ultimi Arrivi</h2>
            <div class="row">
                <c:forEach items="${latestProducts}" var="product">
                    <div class="col-md-4 col-lg-3 mb-4"> <%-- 4 prodotti per riga su schermi grandi, 3 su medi --%>
                        <div class="card product-card-home">
                            <c:url var="productImageUrlHome" value="/images/products/${product.imageUrl != null && not empty product.imageUrl ? product.imageUrl : 'placeholder.png'}"/>
                            <a href="<c:url value='/product/${product.id}'/>">
                                <img class="product-card-img-top" src="${productImageUrlHome}" alt="<c:out value="${product.name}"/>">
                            </a>
                            <div class="card-body">
                                <h5 class="card-title">
                                    <a href="<c:url value='/product/${product.id}'/>" class="text-dark"><c:out value="${product.name}"/></a>
                                </h5>
                                <p class="card-text price">
                                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€ " />
                                </p>
                                    <%--
                                    <p class="card-text small text-muted"><c:out value="${product.category.name}"/></p>
                                    --%>
                            </div>
                            <div class="card-footer text-center">
                                <a href="<c:url value='/product/${product.id}'/>" class="btn btn-outline-secondary btn-sm mr-1">Dettagli</a>
                                <form action="<c:url value='/cart/add'/>" method="post" style="display: inline;">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="quantity" value="1">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button type="submit" class="btn btn-primary btn-sm" ${!product.available || product.stockQuantity <= 0 ? 'disabled' : ''}>
                                        <i class="fas fa-shopping-cart"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div class="text-center mt-4">
                <a href="<c:url value='/products'/>" class="btn btn-outline-secondary btn-lg">Vedi Tutti i Prodotti</a>
            </div>
        </section>
    </c:if>
</div>

<%-- TODO: Aggiungere un Footer --%>
<footer class="py-5 bg-dark text-white mt-5">
    <div class="container text-center">
        <p class="m-0">Copyright © MyShop ${java.time.Year.now()}</p>
        <%-- <p class="m-0"><a href="#" class="text-white-50">Privacy Policy</a> | <a href="#" class="text-white-50">Termini di Servizio</a></p> --%>
    </div>
</footer>


<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>