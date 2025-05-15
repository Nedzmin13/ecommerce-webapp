<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Categorie - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


</head>
<body>
<jsp:include page="../../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-4">
    <h2>Gestione Categorie Prodotti</h2>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
        </div>
    </c:if>

    <div class="mb-3">
        <a href="<c:url value='/admin/categories/new'/>" class="btn btn-primary">
            <i class="fas fa-plus"></i> Aggiungi Categoria
        </a>
    </div>

    <c:choose>
        <c:when test="${not empty categories}">
            <table class="table table-striped table-bordered table-hover">
                <thead class="thead-dark">
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Descrizione</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${categories}" var="category">
                    <tr>
                        <td><c:out value="${category.id}"/></td>
                        <td><c:out value="${category.name}"/></td>
                        <td><c:out value="${category.description}"/></td>
                        <td>
                            <a href="<c:url value='/admin/categories/edit/${category.id}'/>" class="btn btn-sm btn-info" title="Modifica">
                                <i class="fas fa-edit"></i>
                            </a>
                            <form action="<c:url value='/admin/categories/delete/${category.id}'/>" method="post" style="display:inline;"
                                  onsubmit="return confirm('Sei sicuro di voler eliminare la categoria \'${category.name}\'? I prodotti associati potrebbero essere eliminati o necessitare riassegnazione.');">
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
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">Nessuna categoria trovata. <a href="<c:url value='/admin/categories/new'/>">Aggiungine una!</a></div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>