<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        .error-message { color: #dc3545; font-size: 0.875em; display: block; margin-top: .25rem;}
        .current-img-thumbnail { max-width: 150px; max-height: 150px; margin-top: 10px; border: 1px solid #ddd; padding: 5px; border-radius: .25rem;}
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />
<div class="container mt-3"><jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" /></div>

<div class="container mt-4">
    <h2>${pageTitle}</h2>

    <c:if test="${not empty errorMessageGlobal}"><div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-triangle mr-2"></i> ${errorMessageGlobal}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
    </div></c:if>
    <form:errors path="product.*" cssClass="alert alert-danger" element="div" />

    <form:form modelAttribute="product" action="${pageContext.request.contextPath}/admin/products/save" method="post" enctype="multipart/form-data">
        <form:hidden path="id" />
        <%-- Il campo createdAt non dovrebbe essere modificabile, @CreationTimestamp lo gestisce --%>
        <%-- Se non vuoi inviarlo o vederlo nel binding, rimuovilo. Altrimenti, lascialo hidden. --%>
        <%-- <form:hidden path="createdAt" /> --%>

        <%-- CAMPO NASCOSTO PER CONSERVARE L'IMAGEURL ESISTENTE SE NON SI CARICA UN NUOVO FILE --%>
        <form:hidden path="imageUrl" />

        <div class="form-group">
            <form:label path="name">Nome Prodotto</form:label>
            <form:input path="name" cssClass="form-control" required="true" />
            <form:errors path="name" cssClass="error-message" />
        </div>
        <div class="form-group">
            <form:label path="description">Descrizione</form:label>
            <form:textarea path="description" cssClass="form-control" rows="5" />
            <form:errors path="description" cssClass="error-message" />
        </div>
        <div class="row">
            <div class="col-md-4 form-group">
                <form:label path="price">Prezzo (€)</form:label>
                <form:input path="price" cssClass="form-control" type="number" step="0.01" min="0" required="true" />
                <form:errors path="price" cssClass="error-message" />
            </div>
            <div class="col-md-4 form-group">
                <form:label path="stockQuantity">Stock</form:label>
                <form:input path="stockQuantity" cssClass="form-control" type="number" min="0" required="true" />
                <form:errors path="stockQuantity" cssClass="error-message" />
            </div>
            <div class="col-md-4 form-group">
                <form:label path="available">Disponibile</form:label>
                <div class="form-check">
                    <form:checkbox path="available" cssClass="form-check-input" id="availableProduct"/>
                    <label class="form-check-label" for="availableProduct">Sì</label>
                </div>
                <form:errors path="available" cssClass="error-message" />
            </div>
        </div>
        <div class="form-group">
            <form:label path="category.id">Categoria</form:label>
            <form:select path="category.id" cssClass="form-control" required="true">
                <form:option value="" label="--- Seleziona Categoria ---"/>
                <form:options items="${categories}" itemValue="id" itemLabel="name"/>
            </form:select>
            <form:errors path="category.id" cssClass="error-message" />
        </div>

        <div class="form-group">
            <label for="imageFile">Immagine Prodotto</label>
            <input type="file" name="imageFile" id="imageFile" class="form-control-file" accept="image/jpeg, image/png, image/gif"/>
            <small class="form-text text-muted">Carica un nuovo file (JPG, PNG, GIF). Se non selezioni un file, l'immagine attuale (se presente) verrà mantenuta.</small>
                <%-- Visualizza l'immagine attuale se presente --%>
            <c:if test="${not empty product.id && not empty product.imageUrl}">
                <div class="mt-2">
                    <p class="mb-1">Immagine attuale: <c:out value="${product.imageUrl}"/></p>
                    <img src="<c:url value='/images/products/${product.imageUrl}'/>" alt="Immagine attuale" class="current-img-thumbnail"/>
                </div>
            </c:if>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Salva Prodotto</button>
            <a href="<c:url value='/admin/products'/>" class="btn btn-secondary"><i class="fas fa-times"></i> Annulla</a>
        </div>
    </form:form>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>