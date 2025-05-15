package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.Order;

public interface EmailService {

    /**
     * Invia una semplice email di testo.
     * @param to Destinatario dell'email.
     * @param subject Oggetto dell'email.
     * @param text Contenuto testuale dell'email.
     */
    void sendSimpleMessage(String to, String subject, String text);

    /**
     * Invia un'email di conferma registrazione.
     * @param userEmail Email del nuovo utente.
     * @param username Username del nuovo utente.
     * @param siteUrl URL base del sito, per eventuali link di attivazione/login.
     */
    void sendRegistrationConfirmationEmail(String userEmail, String username, String siteUrl);




    void sendOrderConfirmationEmail(Order order);

    void sendOrderStatusUpdateEmail(Order order);

}