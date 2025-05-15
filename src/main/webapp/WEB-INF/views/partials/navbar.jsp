<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%-- Non serve fn:functions qui se non lo usiamo per activePage --%>

<nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <div class="container"> <%-- Manteniamo il container per una buona struttura --%>
        <a class="navbar-brand" href="<c:url value='/'/>">
            MyShop
        </a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item ${activePage == 'home' ? 'active' : ''}">
                    <a class="nav-link" href="<c:url value='/'/>">Home <c:if test="${activePage == 'home'}"><span class="sr-only">(current)</span></c:if></a>
                </li>
                <li class="nav-item ${activePage == 'products' ? 'active' : ''}">
                    <a class="nav-link" href="<c:url value='/products'/>">Prodotti</a>
                </li>
                <sec:authorize access="hasRole('ADMIN')">
                    <%-- Per l'admin, un link generico all'area admin o alla dashboard --%>
                    <li class="nav-item ${activePage == 'adminDashboard' || activePage == 'adminProducts' || activePage == 'adminCategories' || activePage == 'adminOrders' ? 'active' : ''}">
                        <a class="nav-link" href="<c:url value='/admin/dashboard'/>">Admin</a>
                    </li>
                </sec:authorize>
            </ul>
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/cart'/>">
                        <i class="fas fa-shopping-cart"></i> Carrello
                        <c:if test="${not empty shoppingCart && shoppingCart.totalItems > 0}">
                            <span class="badge badge-pill badge-primary">${shoppingCart.totalItems}</span>
                        </c:if>
                    </a>
                </li>
                <sec:authorize access="!isAuthenticated()">
                    <li class="nav-item ${activePage == 'login' ? 'active' : ''}">
                        <a class="nav-link" href="<c:url value='/login'/>">Login</a>
                    </li>
                    <li class="nav-item ${activePage == 'register' ? 'active' : ''}">
                        <a class="nav-link btn btn-outline-primary btn-sm" href="<c:url value='/register'/>">Registrati</a>
                    </li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item dropdown ${activePage == 'customerProfile' || activePage == 'customerOrders' ? 'active' : ''}">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarUserDropdown" role="button"
                           data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <i class="fas fa-user"></i> <sec:authentication property="principal.username" />
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarUserDropdown">
                            <sec:authorize access="hasRole('CUSTOMER')">
                                <a class="dropdown-item ${activePage == 'customerProfile' ? 'active' : ''}" href="<c:url value='/customer/profile'/>">Il Mio Profilo</a>
                                <a class="dropdown-item ${activePage == 'customerOrders' ? 'active' : ''}" href="<c:url value='/customer/orders'/>">I Miei Ordini</a>
                                <div class="dropdown-divider"></div>
                            </sec:authorize>
                            <sec:authorize access="hasRole('ADMIN')">
                                <a class="dropdown-item ${activePage == 'adminDashboard' ? 'active' : ''}" href="<c:url value='/admin/dashboard'/>">Dashboard</a>
                                <a class="dropdown-item ${activePage == 'adminProducts' ? 'active' : ''}" href="<c:url value='/admin/products'/>">Gestione Prodotti</a>
                                <a class="dropdown-item ${activePage == 'adminCategories' ? 'active' : ''}" href="<c:url value='/admin/categories'/>">Gestione Categorie</a>
                                <a class="dropdown-item ${activePage == 'adminOrders' ? 'active' : ''}" href="<c:url value='/admin/orders'/>">Gestione Ordini</a>
                                <div class="dropdown-divider"></div>
                            </sec:authorize>
                            <form id="logoutFormNavOriginal" method="post" action="<c:url value='/logout'/>" style="display: none;">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            </form>
                            <a class="dropdown-item" href="#" onclick="document.getElementById('logoutFormNavOriginal').submit(); return false;">Logout</a>
                        </div>
                    </li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>