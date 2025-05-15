<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Il Mio Profilo - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        .error-message { color: #dc3545; font-size: 0.875em; display: block; margin-top: .25rem;}
        .profile-view dt { font-weight: bold; }
        .profile-view dd { margin-bottom: .75rem; }
        #editProfileFormContainer, #changePasswordFormContainer { display: none; } /* Nascosti di default */
        .profile-section { margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #eee; }
        .profile-section:last-child { border-bottom: none; }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>


<div class="container mt-5">
    <div class="row">
        <div class="col-md-3">
            <%-- Sidebar Menu Cliente --%>
            <h4>Area Cliente</h4>
            <div class="list-group">
                <a href="<c:url value='/customer/profile'/>" class="list-group-item list-group-item-action active">
                    <i class="fas fa-user-circle"></i> Il Mio Profilo
                </a>
                <a href="<c:url value='/customer/orders'/>" class="list-group-item list-group-item-action">
                    <i class="fas fa-history"></i> Cronologia Ordini
                </a>
            </div>
        </div>
        <div class="col-md-9">
            <h2>Il Mio Profilo</h2>
            <hr>

            <%-- Messaggi di Successo/Errore --%>
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

            <%-- SEZIONE VISUALIZZAZIONE PROFILO --%>
            <div id="viewProfileContainer" class="profile-section">
                <c:if test="${not empty user}">
                    <dl class="row profile-view">
                        <dt class="col-sm-4">Username:</dt> <dd class="col-sm-8"><c:out value="${user.username}"/></dd>
                        <dt class="col-sm-4">Nome:</dt> <dd class="col-sm-8"><c:out value="${user.firstName}"/></dd>
                        <dt class="col-sm-4">Cognome:</dt> <dd class="col-sm-8"><c:out value="${user.lastName}"/></dd>
                        <dt class="col-sm-4">Email:</dt> <dd class="col-sm-8"><c:out value="${user.email}"/></dd>
                        <dt class="col-sm-4">Membro dal:</dt>
                        <dd class="col-sm-8">
                            <c:if test="${not empty user.createdAt && not empty dateFormatter}">${user.createdAt.format(dateFormatter)}</c:if>
                        </dd>
                        <dt class="col-sm-4">Tipo Account:</dt>
                        <dd class="col-sm-8"><c:out value="${user.provider}"/></dd>
                    </dl>
                    <button type="button" class="btn btn-primary mr-2" onclick="showEditProfileForm()">
                        <i class="fas fa-edit"></i> Modifica Dati
                    </button>
                    <c:if test="${user.provider == 'LOCAL'}">
                        <button type="button" class="btn btn-secondary" onclick="showChangePasswordForm()">
                            <i class="fas fa-key"></i> Cambia Password
                        </button>
                    </c:if>
                </c:if>
            </div>

            <%-- SEZIONE MODIFICA DATI ANAGRAFICI (Nascosta di default) --%>
            <div id="editProfileFormContainer" class="profile-section">
                <h4>Modifica Dati Anagrafici</h4>
                <form:errors path="userProfileDto.*" cssClass="alert alert-danger" element="div"/>
                <form:form modelAttribute="userProfileDto" action="${pageContext.request.contextPath}/customer/profile/update" method="post">
                    <div class="form-group">
                        <form:label path="firstName">Nome</form:label>
                        <form:input path="firstName" cssClass="form-control" required="true"/>
                        <form:errors path="firstName" cssClass="error-message"/>
                    </div>
                    <div class="form-group">
                        <form:label path="lastName">Cognome</form:label>
                        <form:input path="lastName" cssClass="form-control" required="true"/>
                        <form:errors path="lastName" cssClass="error-message"/>
                    </div>
                    <button type="submit" class="btn btn-success"><i class="fas fa-save"></i> Salva Dati</button>
                    <button type="button" class="btn btn-secondary" onclick="showViewProfile()">Annulla</button>
                </form:form>
            </div>

            <%-- SEZIONE CAMBIO PASSWORD (Nascosta di default, solo per utenti LOCAL) --%>
            <c:if test="${user.provider == 'LOCAL'}">
                <div id="changePasswordFormContainer" class="profile-section">
                    <h4>Cambia Password</h4>
                    <form:errors path="changePasswordDto.*" cssClass="alert alert-danger" element="div"/>
                    <form:form modelAttribute="changePasswordDto" action="${pageContext.request.contextPath}/customer/profile/change-password" method="post">
                        <div class="form-group">
                            <form:label path="currentPassword">Password Attuale</form:label>
                            <form:password path="currentPassword" cssClass="form-control" required="true"/>
                            <form:errors path="currentPassword" cssClass="error-message"/>
                        </div>
                        <div class="form-group">
                            <form:label path="newPassword">Nuova Password</form:label>
                            <form:password path="newPassword" cssClass="form-control" required="true"/>
                            <form:errors path="newPassword" cssClass="error-message"/>
                        </div>
                        <div class="form-group">
                            <form:label path="confirmNewPassword">Conferma Nuova Password</form:label>
                            <form:password path="confirmNewPassword" cssClass="form-control" required="true"/>
                            <form:errors path="confirmNewPassword" cssClass="error-message"/>
                        </div>
                        <button type="submit" class="btn btn-success"><i class="fas fa-key"></i> Aggiorna Password</button>
                        <button type="button" class="btn btn-secondary" onclick="showViewProfile()">Annulla</button>
                    </form:form>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const viewProfileDiv = document.getElementById('viewProfileContainer');
    const editProfileDiv = document.getElementById('editProfileFormContainer');
    const changePasswordDiv = document.getElementById('changePasswordFormContainer');

    function showViewProfile() {
        viewProfileDiv.style.display = 'block';
        editProfileDiv.style.display = 'none';
        if (changePasswordDiv) changePasswordDiv.style.display = 'none';
    }

    function showEditProfileForm() {
        viewProfileDiv.style.display = 'none';
        editProfileDiv.style.display = 'block';
        if (changePasswordDiv) changePasswordDiv.style.display = 'none';
    }

    function showChangePasswordForm() {
        viewProfileDiv.style.display = 'none';
        editProfileDiv.style.display = 'none';
        if (changePasswordDiv) changePasswordDiv.style.display = 'block';
    }


    <c:if test="${not empty org.springframework.validation.BindingResult.userProfileDto && org.springframework.validation.BindingResult.userProfileDto.hasErrors()}">
    showEditProfileForm();
    </c:if>
    <c:if test="${(not empty org.springframework.validation.BindingResult.changePasswordDto && org.springframework.validation.BindingResult.changePasswordDto.hasErrors()) || not empty showChangePasswordFormWithErrors}">
    showChangePasswordForm();
    </c:if>
</script>
</body>
</html>