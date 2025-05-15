package com.myshop.ecommerce.controller.front;

import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.enums.OrderStatus;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller // Non ha un @RequestMapping a livello di classe con /checkout
public class OrderResultController {

    private static final Logger log = LoggerFactory.getLogger(OrderResultController.class);

    private final OrderService orderService;

    @Autowired
    public OrderResultController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint per il redirect JS dopo cattura riuscita
    @GetMapping("/order/success") // Mappato direttamente a /order/success
    public String orderSuccessPage(@RequestParam Long orderId,
                                   @RequestParam(required = false) String transactionId,
                                   Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        log.info("Visualizzazione pagina successo per ordine ID: {}, Transazione PayPal ID: {}", orderId, transactionId);
        Optional<Order> orderOpt = this.orderService.findOrderById(orderId);

        if (orderOpt.isEmpty() || !orderOpt.get().getUser().getUsername().equals(currentUser.getUsername())) {
            log.warn("Tentativo di accesso a pagina successo ordine non valido o non autorizzato. Ordine ID: {}", orderId);
            model.addAttribute("errorMessage", "Ordine non trovato o non sei autorizzato a visualizzarlo.");
            return "error/404"; // Vista di errore 404 che abbiamo
        }

        Order order = orderOpt.get();
        if (order.getStatus() != OrderStatus.PROCESSING && order.getStatus() != OrderStatus.COMPLETED) {
            log.warn("Accesso alla pagina di successo per l'ordine ID {} con stato inatteso: {}", orderId, order.getStatus());
            // Potrebbe comunque mostrare la pagina di successo, o reindirizzare
        }

        model.addAttribute("order", order);
        model.addAttribute("transactionId", transactionId);
        return "front/order-success";
    }


    // Endpoint per il redirect JS dopo errore di cattura PayPal
    // O per quando l'utente viene reindirizzato da PayPal a un URL di errore generico
    @GetMapping("/checkout/payment-error") // Manteniamo /checkout qui per coerenza con il flusso di checkout
    public String paymentErrorPage(@RequestParam Long orderId, // ID del nostro ordine interno
                                   @RequestParam(required = false) String message,
                                   RedirectAttributes redirectAttributes) {
        log.warn("Errore di pagamento per ordine ID: {}. Messaggio: {}", orderId, message);
        redirectAttributes.addFlashAttribute("paymentError", "Si è verificato un errore durante il pagamento: " + (message != null ? message : "Riprova."));
        // Reindirizza alla pagina di conferma per permettere all'utente di riprovare
        return "redirect:/checkout/confirm/" + orderId;
    }


    // Endpoint chiamato da PayPal se l'utente annulla sulla loro pagina
    @GetMapping("/checkout/paypal/cancel/{internalOrderId}")
    public String paypalPaymentCancel(@PathVariable Long internalOrderId, RedirectAttributes redirectAttributes) {
        log.warn("Pagamento PayPal annullato (callback da cancel_url) per ordine interno ID: {}", internalOrderId);
        // L'utente è tornato da PayPal, l'ordine è ancora PENDING.
        // Reindirizziamolo alla pagina di conferma per decidere cosa fare.
        redirectAttributes.addFlashAttribute("paymentError", "Il pagamento con PayPal è stato annullato. Puoi riprovare o annullare l'ordine.");
        return "redirect:/checkout/confirm/" + internalOrderId;
    }

    // Endpoint chiamato da PayPal come return_url dopo successo (fallback se JS fallisce)
    // Questo endpoint riceverà anche parametri come token e PayerID da PayPal
    @GetMapping("/checkout/paypal/success/{internalOrderId}")
    public String paypalReturnUrlSuccess(@PathVariable Long internalOrderId,
                                         @RequestParam(value = "token", required = false) String paypalOrderID, // token è l'orderID di PayPal
                                         @RequestParam(value = "PayerID", required = false) String payerId,
                                         RedirectAttributes redirectAttributes,
                                         Model model, @AuthenticationPrincipal UserDetails currentUser) {

        log.info("Ritorno da PayPal (return_url) dopo successo. Ordine Interno ID: {}, PayPal Order ID (token): {}, PayerID: {}",
                internalOrderId, paypalOrderID, payerId);

        // La logica di cattura dovrebbe essere già avvenuta tramite la chiamata /api/paypal/capture-order dal JS.
        // Questo endpoint è principalmente un fallback o per flussi senza JS pesante.
        // Dovremmo verificare lo stato del nostro ordine interno.
        Optional<Order> orderOpt = orderService.findOrderById(internalOrderId);
        if (orderOpt.isEmpty() || !orderOpt.get().getUser().getUsername().equals(currentUser.getUsername())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ordine non trovato o non autorizzato.");
            return "redirect:/";
        }
        Order order = orderOpt.get();
        if (order.getStatus() == OrderStatus.PROCESSING || order.getStatus() == OrderStatus.COMPLETED) {
            // L'ordine è già stato processato correttamente, reindirizza alla pagina di successo finale
            return "redirect:/order/success?orderId=" + internalOrderId + "&transactionId=" + (order.getPayment() != null ? order.getPayment().getTransactionId() : paypalOrderID);
        } else if (order.getStatus() == OrderStatus.PENDING && paypalOrderID != null) {
            // L'ordine è ancora PENDING, e abbiamo un PayPal Order ID.
            // Questo scenario è meno probabile se il JS onApprove funziona.
            // Potremmo tentare una cattura qui, o semplicemente reindirizzare alla pagina di conferma.
            log.warn("Ordine {} ancora PENDING al ritorno da PayPal success (return_url). PayPal Order ID: {}. Reindirizzamento a conferma.", internalOrderId, paypalOrderID);
            redirectAttributes.addFlashAttribute("checkoutMessage", "Finalizza il tuo pagamento.");
            return "redirect:/checkout/confirm/" + internalOrderId;
        } else {
            // Stato sconosciuto o problematico
            redirectAttributes.addFlashAttribute("errorMessage", "Si è verificato un problema con il tuo ordine.");
            return "redirect:/customer/orders";
        }
    }
}