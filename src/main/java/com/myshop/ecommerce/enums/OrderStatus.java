package com.myshop.ecommerce.enums;

public enum OrderStatus {
    PENDING,       // Ordine ricevuto, in attesa di elaborazione/pagamento
    PROCESSING,    // Pagamento ricevuto, ordine in preparazione
    SHIPPED,       // Ordine spedito
    DELIVERED,     // Ordine consegnato
    CANCELLED,     // Ordine annullato
    PAYMENT_FAILED, // Pagamento fallito
    COMPLETED
}