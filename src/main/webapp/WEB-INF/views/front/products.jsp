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
    <title>${pageTitle} - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        .product-card-img-top { max-height: 180px; width: 100%; object-fit: contain; padding: 10px; margin-bottom: 10px; }
        .product-card { margin-bottom: 30px; height: 100%; display: flex; flex-direction: column; }
        .card-title { min-height: 48px; font-size: 1.1rem; margin-bottom: 0.5rem; }
        .card-body { flex-grow: 1; display: flex; flex-direction: column; }
        .card-text.price { font-size: 1.25rem; font-weight: bold; color: #28a745; margin-bottom: 0.5rem; }
        .card-footer { margin-top: auto; background-color: #fff; border-top: 1px solid rgba(0,0,0,.125); }
        .pagination { justify-content: center; }
        .category-list .list-group-item.active { font-weight: bold; background-color: #007bff; border-color: #007bff; }
        .sidebar-section { margin-bottom: 1.5rem; }
        .sidebar-section h4 { margin-bottom: 0.75rem; font-size: 1.25rem;}
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="../partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-4">
    <div class="row">
        <%-- Sidebar con Filtri --%>
        <div class="col-lg-3">
            <form id="filterForm" action="<c:url value='/products'/>" method="get">
                <%-- Mantiene l'ordinamento corrente quando si applicano i filtri --%>
                <c:if test="${not empty sortField}">
                    <input type="hidden" name="sort" value="${sortField},${sortDir}">
                </c:if>

                <div class="sidebar-section">
                    <h4>Categorie</h4>
                    <div class="list-group category-list">
                        <%-- Link Tutte le Categorie --%>
                        <spring:url value="/products" var="allCatUrl">
                            <c:if test="${not empty keyword}"><spring:param name="keyword" value="${keyword}"/></c:if>
                            <c:if test="${not empty minPrice}"><spring:param name="minPrice" value="${minPrice}"/></c:if>
                            <c:if test="${not empty maxPrice}"><spring:param name="maxPrice" value="${maxPrice}"/></c:if>
                            <c:if test="${not empty sortField}"><spring:param name="sort" value="${sortField},${sortDir}"/></c:if>
                        </spring:url>
                        <a href="${allCatUrl}" class="list-group-item list-group-item-action ${currentCategoryId == null ? 'active' : ''}"
                           onclick="document.getElementById('filterFormCategory').value=''; document.getElementById('filterForm').submit(); return false;">
                            Tutte le Categorie
                        </a>
                        <c:forEach items="${categories}" var="cat">
                            <spring:url value="/products" var="catUrl">
                                <spring:param name="category" value="${cat.id}"/>
                                <c:if test="${not empty keyword}"><spring:param name="keyword" value="${keyword}"/></c:if>
                                <c:if test="${not empty minPrice}"><spring:param name="minPrice" value="${minPrice}"/></c:if>
                                <c:if test="${not empty maxPrice}"><spring:param name="maxPrice" value="${maxPrice}"/></c:if>
                                <c:if test="${not empty sortField}"><spring:param name="sort" value="${sortField},${sortDir}"/></c:if>
                            </spring:url>
                            <a href="${catUrl}" class="list-group-item list-group-item-action ${currentCategoryId == cat.id ? 'active' : ''}"
                               onclick="document.getElementById('filterFormCategory').value='${cat.id}'; document.getElementById('filterForm').submit(); return false;">
                                <c:out value="${cat.name}"/>
                            </a>
                        </c:forEach>
                    </div>
                    <input type="hidden" name="category" id="filterFormCategory" value="${currentCategoryId}">
                </div>

                <div class="sidebar-section">
                    <h4>Cerca</h4>
                    <div class="input-group">
                        <input type="text" name="keyword" class="form-control" placeholder="Nome o descrizione..." value="<c:out value='${keyword}'/>">
                    </div>
                </div>

                <div class="sidebar-section">
                    <h4>Prezzo</h4>
                    <div class="form-row">
                        <div class="col">
                            <label for="minPrice" class="sr-only">Min</label>
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend"><span class="input-group-text">€</span></div>
                                <input type="number" name="minPrice" id="minPrice" class="form-control" placeholder="Min" value="${minPrice}" step="0.01" min="0">
                            </div>
                        </div>
                        <div class="col">
                            <label for="maxPrice" class="sr-only">Max</label>
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend"><span class="input-group-text">€</span></div>
                                <input type="number" name="maxPrice" id="maxPrice" class="form-control" placeholder="Max" value="${maxPrice}" step="0.01" min="0">
                            </div>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-block"><i class="fas fa-filter"></i> Applica Filtri</button>
                <a href="<c:url value='/products'/>" class="btn btn-outline-secondary btn-block mt-2">Resetta Filtri</a>
            </form>
        </div>

        <%-- Contenuto Principale: Lista Prodotti --%>
        <div class="col-lg-9">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="mb-0">${pageTitle}</h2>
                <form id="sortOnlyForm" action="<c:url value='/products'/>" method="get" class="form-inline">
                    <c:if test="${not empty currentCategoryId}"><input type="hidden" name="category" value="${currentCategoryId}"></c:if>
                    <c:if test="${not empty keyword}"><input type="hidden" name="keyword" value="${keyword}"></c:if>
                    <c:if test="${not empty minPrice}"><input type="hidden" name="minPrice" value="${minPrice}"></c:if>
                    <c:if test="${not empty maxPrice}"><input type="hidden" name="maxPrice" value="${maxPrice}"></c:if>
                    <label for="sortSelect" class="mr-2">Ordina per:</label>
                    <select id="sortSelect" name="sort" class="custom-select custom-select-sm" onchange="this.form.submit();">
                        <option value="name,asc" ${sortField == 'name' && sortDir == 'asc' ? 'selected' : ''}>Nome (A-Z)</option>
                        <option value="name,desc" ${sortField == 'name' && sortDir == 'desc' ? 'selected' : ''}>Nome (Z-A)</option>
                        <option value="price,asc" ${sortField == 'price' && sortDir == 'asc' ? 'selected' : ''}>Prezzo (Crescente)</option>
                        <option value="price,desc" ${sortField == 'price' && sortDir == 'desc' ? 'selected' : ''}>Prezzo (Decrescente)</option>
                        <option value="createdAt,desc" ${sortField == 'createdAt' && sortDir == 'desc' ? 'selected' : ''}>Novità</option>
                    </select>
                </form>
            </div>

            <c:choose>
                <c:when test="${productPage != null && productPage.hasContent()}">
                    <div class="row">
                        <c:forEach items="${productPage.content}" var="product">
                            <div class="col-md-6 col-lg-4">
                                <div class="card product-card">
                                    <c:url var="productImageUrl" value="/images/products/${product.imageUrl != null && not empty product.imageUrl ? product.imageUrl : 'placeholder.png'}"/>
                                    <a href="<c:url value='/product/${product.id}'/>">
                                        <img class="product-card-img-top" src="${productImageUrl}" alt="<c:out value="${product.name}"/>">
                                    </a>
                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <a href="<c:url value='/product/${product.id}'/>"><c:out value="${product.name}"/></a>
                                        </h5>
                                        <h6 class="card-subtitle mb-2 text-muted"><c:out value="${product.category.name}"/></h6>
                                        <p class="card-text price">
                                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€ " />
                                        </p>
                                        <p class="card-text small ${product.available && product.stockQuantity > 0 ? 'text-success' : 'text-danger'}">
                                            <c:choose>
                                                <c:when test="${product.available && product.stockQuantity > 0}">
                                                    <i class="fas fa-check-circle"></i> Disponibile
                                                </c:when>
                                                <c:otherwise>
                                                    <i class="fas fa-times-circle"></i> Non disponibile
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                    <div class="card-footer">
                                        <a href="<c:url value='/product/${product.id}'/>" class="btn btn-outline-secondary btn-sm mr-1"><i class="fas fa-search-plus"></i> Dettagli</a>
                                        <form action="<c:url value='/cart/add'/>" method="post" style="display: inline;">
                                            <input type="hidden" name="productId" value="${product.id}">
                                            <input type="hidden" name="quantity" value="1">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <button type="submit" class="btn btn-primary btn-sm" ${!product.available || product.stockQuantity <= 0 ? 'disabled' : ''}>
                                                <i class="fas fa-shopping-cart"></i> Aggiungi
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <nav aria-label="Product navigation" class="mt-4">
                        <ul class="pagination">
                            <li class="page-item ${productPage.isFirst() ? 'disabled' : ''}">
                                <spring:url value="/products" var="prevUrl"><c:if test="${not empty currentCategoryId}"><spring:param name="category" value="${currentCategoryId}"/></c:if><c:if test="${not empty keyword}"><spring:param name="keyword" value="${keyword}"/></c:if><c:if test="${not empty minPrice}"><spring:param name="minPrice" value="${minPrice}"/></c:if><c:if test="${not empty maxPrice}"><spring:param name="maxPrice" value="${maxPrice}"/></c:if><spring:param name="page" value="${productPage.number - 1}"/><spring:param name="size" value="${productPage.size}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                                <a class="page-link" href="${prevUrl}">«</a>
                            </li>
                            <c:forEach begin="0" end="${productPage.totalPages - 1}" var="i">
                                <spring:url value="/products" var="pageUrl"><c:if test="${not empty currentCategoryId}"><spring:param name="category" value="${currentCategoryId}"/></c:if><c:if test="${not empty keyword}"><spring:param name="keyword" value="${keyword}"/></c:if><c:if test="${not empty minPrice}"><spring:param name="minPrice" value="${minPrice}"/></c:if><c:if test="${not empty maxPrice}"><spring:param name="maxPrice" value="${maxPrice}"/></c:if><spring:param name="page" value="${i}"/><spring:param name="size" value="${productPage.size}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                                <li class="page-item ${productPage.number == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageUrl}">${i + 1}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${productPage.isLast() ? 'disabled' : ''}">
                                <spring:url value="/products" var="nextUrl"><c:if test="${not empty currentCategoryId}"><spring:param name="category" value="${currentCategoryId}"/></c:if><c:if test="${not empty keyword}"><spring:param name="keyword" value="${keyword}"/></c:if><c:if test="${not empty minPrice}"><spring:param name="minPrice" value="${minPrice}"/></c:if><c:if test="${not empty maxPrice}"><spring:param name="maxPrice" value="${maxPrice}"/></c:if><spring:param name="page" value="${productPage.number + 1}"/><spring:param name="size" value="${productPage.size}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                                <a class="page-link" href="${nextUrl}">»</a>
                            </li>
                        </ul>
                    </nav>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info text-center mt-3" role="alert">
                        <i class="fas fa-info-circle fa-2x mb-2"></i><br>
                        Nessun prodotto trovato
                        <c:if test="${not empty keyword}"> per "<c:out value='${keyword}'/>"</c:if>
                        <c:if test="${not empty currentCategoryName}"> in <c:out value='${currentCategoryName}'/></c:if>
                        <c:if test="${not empty minPrice}"> da €${minPrice}</c:if>
                        <c:if test="${not empty maxPrice}"> a €${maxPrice}</c:if>.
                        <br>
                        Prova a modificare i filtri o <a href="<c:url value='/products'/>" class="alert-link">visualizza tutti i prodotti</a>.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Script per gestire i link delle categorie in modo che inviino il form dei filtri
    // Questo è un po' un workaround per non dover duplicare tutti i parametri nei link <a> delle categorie.
    // Alternativa: usare solo il pulsante "Applica Filtri".
    // L'onclick sui link delle categorie ora imposta un campo hidden e invia il form principale.
    // Questo mantiene i valori di keyword e prezzo inseriti.
</script>
</body>
</html>