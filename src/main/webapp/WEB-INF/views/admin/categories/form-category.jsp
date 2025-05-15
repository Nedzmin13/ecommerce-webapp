<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        .error-message { color: #dc3545; font-size: 0.875em; display: block; margin-top: .25rem;}
    </style>
</head>
<body>
<jsp:include page="../../partials/navbar.jsp" />

<div class="container mt-3"> <%-- Usa container o container-fluid a seconda del layout --%>
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-4">
    <h2>${pageTitle}</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>
    <form:errors path="category.*" cssClass="alert alert-danger" element="div" />


    <form:form modelAttribute="category" action="${pageContext.request.contextPath}/admin/categories/save" method="post">
        <form:hidden path="id" />

        <div class="form-group">
            <form:label path="name">Nome Categoria</form:label>
            <form:input path="name" cssClass="form-control" required="true" />
            <form:errors path="name" cssClass="error-message" />
        </div>

        <div class="form-group">
            <form:label path="description">Descrizione</form:label>
            <form:textarea path="description" cssClass="form-control" rows="3" />
            <form:errors path="description" cssClass="error-message" />
        </div>

        <div class="mt-3">
            <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Salva Categoria</button>
            <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary"><i class="fas fa-times"></i> Annulla</a>
        </div>
    </form:form>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>