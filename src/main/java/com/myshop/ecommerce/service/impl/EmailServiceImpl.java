package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.OrderItem;
import com.myshop.ecommerce.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.site.name:MyShop E-commerce}")
    private String siteName;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email inviata con successo a: {}, Oggetto: {}", to, subject);
        } catch (MailException e) {
            log.error("Errore durante l'invio dell'email a {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendRegistrationConfirmationEmail(String userEmail, String username, String siteUrl) {
        // ... (codice esistente invariato) ...
        String subject = "Benvenuto su " + siteName + "!";
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Ciao ").append(username).append(",\n\n");
        textBuilder.append("Grazie per esserti registrato su ").append(siteName).append(".\n\n");
        textBuilder.append("Il tuo account è stato creato con successo.\n");
        textBuilder.append("Puoi accedere al tuo account visitando il seguente link:\n");
        textBuilder.append(siteUrl).append("/login\n\n");
        textBuilder.append("Cordiali saluti,\n");
        textBuilder.append("Il Team di ").append(siteName);

        sendSimpleMessage(userEmail, subject, textBuilder.toString());
    }

    // --- NUOVO METODO IMPLEMENTATO ---
    @Override
    public void sendOrderConfirmationEmail(Order order) {
        if (order == null || order.getUser() == null) {
            log.error("Impossibile inviare email di conferma ordine: dati ordine o utente mancanti.");
            return;
        }

        String to = order.getUser().getEmail();
        String subject = "Conferma Ordine #" + order.getOrderNumber() + " su " + siteName;

        // Formattatori per data e valuta
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY);

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Ciao ").append(order.getUser().getFirstName()).append(",\n\n");
        textBuilder.append("Grazie per il tuo acquisto su ").append(siteName).append("!\n\n");
        textBuilder.append("Il tuo ordine #").append(order.getOrderNumber()).append(" è stato confermato e verrà elaborato a breve.\n\n");
        textBuilder.append("DETTAGLI ORDINE:\n");
        textBuilder.append("--------------------------------------------------\n");
        textBuilder.append("Data Ordine: ").append(order.getOrderDate().format(dateFormatter)).append("\n");
        textBuilder.append("Numero Ordine: ").append(order.getOrderNumber()).append("\n");
        textBuilder.append("Stato Ordine: ").append(order.getStatus().toString()).append("\n\n"); // Potremmo voler tradurre lo stato

        textBuilder.append("ARTICOLI ORDINATI:\n");
        for (OrderItem item : order.getOrderItems()) {
            textBuilder.append("- ").append(item.getProduct().getName())
                    .append(" (Quantità: ").append(item.getQuantity()).append(")")
                    .append(" - Prezzo Unit.: ").append(currencyFormatter.format(item.getPricePerUnit()))
                    .append(" - Subtotale: ").append(currencyFormatter.format(item.getTotalPrice()))
                    .append("\n");
        }
        textBuilder.append("\n");

        textBuilder.append("IMPORTO TOTALE: ").append(currencyFormatter.format(order.getTotalAmount())).append("\n\n");

        if (order.getShipping() != null) {
            textBuilder.append("INDIRIZZO DI SPEDIZIONE:\n");

            textBuilder.append(order.getUser().getFirstName()).append(" ").append(order.getUser().getLastName()).append("\n");
            textBuilder.append(order.getShipping().getAddressLine1()).append("\n");
            if (order.getShipping().getAddressLine2() != null && !order.getShipping().getAddressLine2().isEmpty()) {
                textBuilder.append(order.getShipping().getAddressLine2()).append("\n");
            }
            textBuilder.append(order.getShipping().getPostalCode()).append(" ").append(order.getShipping().getCity())
                    .append(" (").append(order.getShipping().getState()).append(")\n");
            textBuilder.append(order.getShipping().getCountry()).append("\n");
            if (order.getShipping().getPhone() != null && !order.getShipping().getPhone().isEmpty()) {
                textBuilder.append("Tel: ").append(order.getShipping().getPhone()).append("\n");
            }
            textBuilder.append("\n");
        }

        if (order.getPayment() != null) {
            textBuilder.append("DETTAGLI PAGAMENTO:\n");
            textBuilder.append("Metodo: ").append(order.getPayment().getPaymentMethod().toString()).append("\n");
            textBuilder.append("ID Transazione: ").append(order.getPayment().getTransactionId()).append("\n");
            textBuilder.append("Data Pagamento: ").append(order.getPayment().getPaymentDate() != null ? order.getPayment().getPaymentDate().format(dateFormatter) : "N/D").append("\n\n");
        }

        textBuilder.append("Puoi visualizzare i dettagli del tuo ordine e il suo stato accedendo al tuo account sul nostro sito.\n\n");
        textBuilder.append("Cordiali saluti,\n");
        textBuilder.append("Il Team di ").append(siteName);

        sendSimpleMessage(to, subject, textBuilder.toString());
    }

    @Override
    public void sendOrderStatusUpdateEmail(Order order) {
        if (order == null || order.getUser() == null || order.getStatus() == null) {
            log.error("Impossibile inviare email di aggiornamento stato ordine: dati mancanti.");
            return;
        }

        String to = order.getUser().getEmail();
        String subject = "Aggiornamento Stato Ordine #" + order.getOrderNumber() + " - " + siteName;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ITALY);

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Ciao ").append(order.getUser().getFirstName()).append(",\n\n");
        textBuilder.append("Ci sono aggiornamenti per il tuo ordine #").append(order.getOrderNumber()).append(" su ").append(siteName).append(".\n\n");

        String nuovoStato = order.getStatus().toString();
        switch (order.getStatus()) {
            case PROCESSING:
                nuovoStato = "In Preparazione";
                textBuilder.append("Il tuo ordine è ora: ").append(nuovoStato).append(".\n");
                textBuilder.append("Stiamo preparando i tuoi articoli per la spedizione.\n");
                break;
            case SHIPPED:
                nuovoStato = "Spedito";
                textBuilder.append("Buone notizie! Il tuo ordine è stato ").append(nuovoStato).append(".\n");
                break;
            case DELIVERED:
                nuovoStato = "Consegnato";
                textBuilder.append("Il tuo ordine è stato ").append(nuovoStato).append(".\n");
                textBuilder.append("Speriamo ti piacciano i tuoi nuovi acquisti!\n");
                break;
            case CANCELLED:
                nuovoStato = "Annullato";
                textBuilder.append("Il tuo ordine è stato ").append(nuovoStato).append(".\n");
                textBuilder.append("Se hai domande, non esitare a contattarci.\n");
                break;
            case PAYMENT_FAILED:
                nuovoStato = "Pagamento Fallito";
                textBuilder.append("Si è verificato un problema con il pagamento del tuo ordine: ").append(nuovoStato).append(".\n");
                textBuilder.append("Per favore, controlla i tuoi dati di pagamento o contattaci per assistenza.\n");
                break;
            case COMPLETED:
                nuovoStato = "Completato";
                textBuilder.append("Il tuo ordine è stato segnato come ").append(nuovoStato).append(".\n");
                break;
            default:
                textBuilder.append("Il nuovo stato del tuo ordine è: ").append(nuovoStato).append(".\n");
                break;
        }
        textBuilder.append("\n");

        textBuilder.append("Puoi visualizzare i dettagli completi del tuo ordine accedendo al tuo account:\n");

        textBuilder.append("Grazie per aver scelto ").append(siteName).append(".\n\n");

        textBuilder.append("Cordiali saluti,\n");
        textBuilder.append("Il Team di ").append(siteName);

        sendSimpleMessage(to, subject, textBuilder.toString());
    }
}