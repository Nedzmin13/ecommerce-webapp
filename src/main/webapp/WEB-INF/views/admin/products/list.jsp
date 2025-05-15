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
    <title>Gestione Prodotti - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        .table th, .table td { vertical-align: middle; }
        .product-img-thumbnail { max-width: 60px; height: auto; border-radius: .25rem; }
        .action-buttons .btn { margin-right: 5px; }
        .action-buttons form { margin-right: 5px; }
        .page-item.disabled .page-link { color: #6c757d; }
        .page-item.active .page-link { z-index: 3; color: #fff; background-color: #007bff; border-color: #007bff;}
    </style>
</head>
<body>
<jsp:include page="../../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="fas fa-box-open"></i> Gestione Prodotti</h1>
    </div>

    <%-- Messaggi di Successo/Errore --%>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle mr-2"></i> ${successMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle mr-2"></i> ${errorMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <div class="mb-3">
        <a href="<c:url value='/admin/products/new'/>" class="btn btn-primary">
            <i class="fas fa-plus"></i> Aggiungi Nuovo Prodotto
        </a>
    </div>

    <c:choose>
        <c:when test="${not empty productPage && productPage.hasContent()}">
            <div class="table-responsive">
                <table class="table table-striped table-bordered table-hover">
                    <thead class="thead-dark">
                    <tr>
                        <th>
                            <spring:url value="/admin/products" var="sortByIdUrl"><spring:param name="page" value="${currentPage}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="id,${sortField == 'id' ? reverseSortDir : 'asc'}"/></spring:url>
                            <a href="${sortByIdUrl}" class="text-white">ID</a> <c:if test="${sortField == 'id'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'} text-white-50"></i></c:if>
                        </th>
                        <th>Immagine</th>
                        <th>
                            <spring:url value="/admin/products" var="sortByNameUrl"><spring:param name="page" value="${currentPage}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="name,${sortField == 'name' ? reverseSortDir : 'asc'}"/></spring:url>
                            <a href="${sortByNameUrl}" class="text-white">Nome</a> <c:if test="${sortField == 'name'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'} text-white-50"></i></c:if>
                        </th>
                        <th>Categoria</th>
                        <th class="text-right">
                            <spring:url value="/admin/products" var="sortByPriceUrl"><spring:param name="page" value="${currentPage}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="price,${sortField == 'price' ? reverseSortDir : 'asc'}"/></spring:url>
                            <a href="${sortByPriceUrl}" class="text-white">Prezzo</a> <c:if test="${sortField == 'price'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'} text-white-50"></i></c:if>
                        </th>
                        <th class="text-center">
                            <spring:url value="/admin/products" var="sortByStockUrl"><spring:param name="page" value="${currentPage}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="stockQuantity,${sortField == 'stockQuantity' ? reverseSortDir : 'asc'}"/></spring:url>
                            <a href="${sortByStockUrl}" class="text-white">Stock</a> <c:if test="${sortField == 'stockQuantity'}"><i class="fas fa-sort-${sortDir == 'asc' ? 'up' : 'down'} text-white-50"></i></c:if>
                        </th>
                        <th class="text-center">Disponibile</th>
                        <th class="text-center">Azioni</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${productPage.content}" var="product">
                        <tr>
                            <td><c:out value="${product.id}"/></td>
                            <td class="text-center">
                                <c:url var="imgUrl" value="/images/products/${product.imageUrl != null && not empty product.imageUrl ? product.imageUrl : 'placeholder.png'}"/>
                                <img src="${imgUrl}" alt="<c:out value='${product.name}'/>" class="product-img-thumbnail">
                            </td>
                            <td><c:out value="${product.name}"/></td>
                            <td><c:out value="${product.category.name}"/></td>
                            <td class="text-right"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="€ "/></td>
                            <td class="text-center"><c:out value="${product.stockQuantity}"/></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${product.available}">
                                        <span class="badge badge-success">Sì</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">No</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center action-buttons">
                                <a href="<c:url value='/admin/products/edit/${product.id}'/>" class="btn btn-sm btn-info" title="Modifica">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <form action="<c:url value='/admin/products/delete/${product.id}'/>" method="post" style="display:inline;"
                                      onsubmit="return confirm('Sei sicuro di voler eliminare il prodotto \'${product.name}\'? L\'azione è irreversibile.');">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button type="submit" class="btn btn-sm btn-danger" title="Elimina">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <%-- Paginazione --%>
            <nav aria-label="Product navigation admin" class="mt-3">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${productPage.isFirst() ? 'disabled' : ''}">
                        <spring:url value="/admin/products" var="prevUrl"><spring:param name="page" value="${productPage.number - 1}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                        <a class="page-link" href="${prevUrl}">« Precedente</a>
                    </li>
                    <c:forEach var="i" begin="0" end="${productPage.totalPages - 1}">
                        <spring:url value="/admin/products" var="pageLinkUrl"><spring:param name="page" value="${i}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                        <li class="page-item ${orderPage.number == i ? 'active' : ''}">
                            <a class="page-link" href="${pageLinkUrl}">${i + 1}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${productPage.isLast() ? 'disabled' : ''}">
                        <spring:url value="/admin/products" var="nextUrl"><spring:param name="page" value="${productPage.number + 1}"/><spring:param name="size" value="${pageSize}"/><spring:param name="sort" value="${sortField},${sortDir}"/></spring:url>
                        <a class="page-link" href="${nextUrl}">Successiva »</a>
                    </li>
                </ul>
            </nav>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info mt-3">Nessun prodotto trovato. <a href="<c:url value='/admin/products/new'/>" class="alert-link">Aggiungine uno!</a></div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>