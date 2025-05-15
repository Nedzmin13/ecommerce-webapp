<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        .dashboard-card .card-body {
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .dashboard-card .card-title {
            font-size: 1.5rem;
        }
        .dashboard-card .fas {
            font-size: 2.5em;
            opacity: 0.3;
            position: absolute;
            right: 15px;
            top: 25px;
        }
    </style>
</head>
<body>
<jsp:include page="../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-4">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="fas fa-tachometer-alt"></i> ${pageTitle}</h1>
    </div>

    <p>Benvenuto nell'area di amministrazione di MyShop, <sec:authentication property="principal.username"/>!</p>
    <p>Da qui puoi gestire i contenuti e le operazioni del tuo negozio online.</p>



    <h4>Accesso Rapido</h4>
    <div class="row mt-3">
        <div class="col-md-4 mb-3">
            <div class="card dashboard-card text-white bg-primary h-100">
                <div class="card-body">
                    <div>
                        <h5 class="card-title">Prodotti</h5>
                        <p class="card-text">Gestisci il catalogo, aggiungi, modifica o rimuovi prodotti.</p>
                    </div>
                    <a href="<c:url value='/admin/products'/>" class="btn btn-light mt-auto stretched-link">Vai ai Prodotti <i class="fas fa-arrow-circle-right"></i></a>
                </div>
                <i class="fas fa-boxes"></i>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card dashboard-card text-white bg-success h-100">
                <div class="card-body">
                    <div>
                        <h5 class="card-title">Ordini</h5>
                        <p class="card-text">Visualizza e gestisci gli ordini dei clienti, aggiorna lo stato.</p>
                    </div>
                    <a href="<c:url value='/admin/orders'/>" class="btn btn-light mt-auto stretched-link">Vai agli Ordini <i class="fas fa-arrow-circle-right"></i></a>
                </div>
                <i class="fas fa-receipt"></i>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card dashboard-card text-white bg-info h-100">
                <div class="card-body">
                    <div>
                        <h5 class="card-title">Categorie</h5>
                        <p class="card-text">Organizza i tuoi prodotti creando e modificando le categorie.</p>
                    </div>
                    <a href="<c:url value='/admin/categories'/>" class="btn btn-light mt-auto stretched-link">Vai alle Categorie <i class="fas fa-arrow-circle-right"></i></a>
                </div>
                <i class="fas fa-tags"></i>
            </div>
        </div>
    </div>
</div>

<%-- <jsp:include page="../partials/footer.jsp" /> --%>
<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>