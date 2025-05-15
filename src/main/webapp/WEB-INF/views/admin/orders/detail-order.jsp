<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page import="com.myshop.ecommerce.enums.OrderStatus" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Dettaglio Ordine Admin #${order.orderNumber} - MyShop</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

</head>
<body>
<jsp:include page="../../partials/navbar.jsp" />

<div class="container mt-3">
    <jsp:include page="/WEB-INF/views/partials/_breadcrumbs.jsp" />
</div>

<div class="container mt-5">
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

    <c:if test="${not empty order}">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2>Dettaglio Ordine Admin: #<c:out value="${order.orderNumber}"/></h2>
            <a href="<c:url value='/admin/orders'/>" class="btn btn-outline-secondary"><i class="fas fa-arrow-left"></i> Torna alla Lista</a>
        </div>
        <hr>

        <div class="card mb-4">
            <div class="card-header"><h4>Informazioni Ordine</h4></div>
            <div class="card-body">
                <p><strong>Cliente:</strong> <c:out value="${order.user.firstName} ${order.user.lastName}"/> (<c:out value="${order.user.email}"/>)</p>
                <p><strong>Data Ordine:</strong> ${order.orderDate.format(dateFormatter)}</p>
                <p><strong>Stato Attuale:</strong>
                    <span class="badge badge-lg badge-${order.status == 'DELIVERED' or order.status == 'COMPLETED' ? 'success' : (order.status == 'CANCELLED' or order.status == 'PAYMENT_FAILED' ? 'danger' : (order.status == 'SHIPPED' ? 'info' : (order.status == 'PROCESSING' ? 'primary' : 'warning')))}">
                            <c:out value="${order.status}"/>
                        </span>
                </p>
                <p><strong>Totale Ordine:</strong> <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="€ "/></p>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 mb-4">
                <div class="card h-100">
                    <div class="card-header"><h4>Indirizzo di Spedizione</h4></div>
                    <div class="card-body">
                        <address>
                            <strong><c:out value="${order.user.firstName} ${order.user.lastName}"/></strong><br>
                            <c:out value="${order.shipping.addressLine1}"/><br>
                            <c:if test="${not empty order.shipping.addressLine2}"><c:out value="${order.shipping.addressLine2}"/><br></c:if>
                            <c:out value="${order.shipping.postalCode}"/> <c:out value="${order.shipping.city}"/> (<c:out value="${order.shipping.state}"/>)<br>
                            <c:out value="${order.shipping.country}"/><br>
                            <c:if test="${not empty order.shipping.phone}">Tel: <c:out value="${order.shipping.phone}"/></c:if>
                        </address>
                    </div>
                </div>
            </div>
            <c:if test="${not empty order.payment}">
                <div class="col-md-6 mb-4">
                    <div class="card h-100">
                        <div class="card-header"><h4>Dettagli Pagamento</h4></div>
                        <div class="card-body">
                            <p><strong>Metodo:</strong> <c:out value="${order.payment.paymentMethod}"/></p>
                            <p><strong>ID Transazione PayPal:</strong> <c:out value="${order.payment.transactionId}"/></p>
                            <p><strong>Data Pagamento:</strong>
                                <c:if test="${not empty order.payment.paymentDate}">${order.payment.paymentDate.format(dateFormatter)}</c:if>
                                <c:if test="${empty order.payment.paymentDate}">N/D</c:if>
                            </p>
                            <p><strong>Stato Pagamento:</strong> <c:out value="${order.payment.status}"/></p>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

        <div class="card mt-2">
            <div class="card-header"><h4>Articoli Ordinati</h4></div>
            <div class="card-body p-0">
                <table class="table table-striped table-bordered mb-0">
                    <thead class="thead-light">
                    <tr>
                        <th>Prodotto</th>
                        <th class="text-center">Quantità</th>
                        <th class="text-right">Prezzo Unit.</th>
                        <th class="text-right">Subtotale</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${order.orderItems}" var="item">
                        <tr>
                            <td>
                                <a href="<c:url value='/product/${item.product.id}'/>" target="_blank">
                                    <c:out value="${item.product.name}"/>
                                </a>
                            </td>
                            <td class="text-center"><c:out value="${item.quantity}"/></td>
                            <td class="text-right"><fmt:formatNumber value="${item.pricePerUnit}" type="currency" currencySymbol="€ "/></td>
                            <td class="text-right"><fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="€ "/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <%-- SEZIONE PER AGGIORNARE STATO ORDINE --%>
        <div class="card mt-4">
            <div class="card-header">
                <h4><i class="fas fa-edit"></i> Aggiorna Stato Ordine</h4>
            </div>
            <div class="card-body">
                <form action="<c:url value='/admin/orders/update-status/${order.id}'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <div class="form-row align-items-center">
                        <div class="col-md-8">
                            <div class="form-group">
                                <label for="newStatus" class="sr-only">Nuovo Stato:</label>
                                <select name="newStatus" id="newStatus" class="form-control">

                                    <c:forEach items="${orderStatusValues}" var="statusOpt">
                                        <option value="${statusOpt}" ${statusOpt == order.status ? 'selected' : ''}>
                                            <c:out value="${statusOpt}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <button type="submit" class="btn btn-primary btn-block">Aggiorna Stato</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>