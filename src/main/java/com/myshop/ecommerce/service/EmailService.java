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

    // Potremmo aggiungere altri metodi per email più complesse (HTML, allegati, template) in futuro
    // void sendHtmlMessage(String to, String subject, String htmlBody);
    // void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);


    void sendOrderConfirmationEmail(Order order);

    void sendOrderStatusUpdateEmail(Order order); // NUOVO METODO

}