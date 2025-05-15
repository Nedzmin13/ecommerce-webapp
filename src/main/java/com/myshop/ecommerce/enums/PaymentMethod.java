package com.myshop.ecommerce.enums;

public enum PaymentMethod {
    PAYPAL,
    CREDIT_CARD, // Potremmo dettagliare ulteriormente o usare un provider come Stripe
    BANK_TRANSFER,
    CASH_ON_DELIVERY // Se supportato
}