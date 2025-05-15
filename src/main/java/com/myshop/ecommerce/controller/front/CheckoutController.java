package com.myshop.ecommerce.controller.front;

import com.myshop.ecommerce.dto.ShippingAddressDto;
import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.OrderStatus; // Necessario per cancelPendingOrder
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.Cart;
import com.myshop.ecommerce.repository.UserRepository;
import com.myshop.ecommerce.service.CartService;
import com.myshop.ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
// L'import Optional non è più necessario se si usa orElseThrow()
// import java.util.Optional;

@Controller
@RequestMapping("/checkout") // Tutte le richieste a questo controller iniziano con /checkout
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @Value("${paypal.client.id}")
    private String paypalClientId;

    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService, UserRepository userRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showCheckoutPage(HttpSession session, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            log.warn("Tentativo di accesso a /checkout da utente non autenticato.");
            return "redirect:/login?redirect=/checkout";
        }

        Cart cart = cartService.getCart(session);
        if (cart.getItems().isEmpty()) {
            log.info("Tentativo di checkout con carrello vuoto. Utente: {}", currentUser.getUsername());
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        if (!model.containsAttribute("shippingAddressDto")) { // Evita sovrascrittura se viene da un POST con errori
            model.addAttribute("shippingAddressDto", new ShippingAddressDto());
        }

        log.debug("Visualizzazione pagina checkout per utente: {}. Carrello items: {}", currentUser.getUsername(), cart.getTotalItems());
        return "front/checkout";
    }

    @PostMapping("/submit-address")
    public String processShippingAddress(
            @ModelAttribute("shippingAddressDto") @Valid ShippingAddressDto shippingAddressDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            @AuthenticationPrincipal UserDetails currentUserDetails,
            RedirectAttributes redirectAttributes) {

        log.info("Processo indirizzo di spedizione per utente: {}", currentUserDetails.getUsername());
        Cart cart = cartService.getCart(session);

        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            log.warn("Errore di validazione per indirizzo di spedizione: {}", bindingResult.getAllErrors());
            model.addAttribute("cart", cart); // Ripassa il carrello alla vista per il riepilogo
            return "front/checkout"; // Ritorna al form mostrando gli errori
        }

        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUserDetails.getUsername()));

        try {
            Order createdOrder = orderService.createOrder(cart, currentUser, shippingAddressDto);
            session.setAttribute("pendingOrderId", createdOrder.getId());
            session.removeAttribute("pendingShippingAddress"); // Non più necessario se l'abbiamo usato

            log.info("Ordine ID {} creato per utente {}. Reindirizzamento a conferma pagamento.", createdOrder.getId(), currentUser.getUsername());
            redirectAttributes.addFlashAttribute("checkoutMessage", "Il tuo ordine è stato registrato. Conferma e procedi al pagamento.");
            return "redirect:/checkout/confirm/" + createdOrder.getId();

        } catch (IllegalStateException | ResourceNotFoundException e) {
            log.warn("Errore durante la creazione dell'ordine: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("cartErrorMessage", e.getMessage());
            return "redirect:/checkout"; // Torna alla pagina di checkout per correggere (es. stock)
        } catch (Exception e) {
            log.error("Errore imprevisto durante la creazione dell'ordine per l'utente {}", currentUser.getUsername(), e);
            model.addAttribute("errorMessage", "Si è verificato un errore imprevisto durante la creazione dell'ordine.");
            model.addAttribute("cart", cart);
            return "front/checkout"; // Ritorna al form con un messaggio di errore generico
        }
    }

    @GetMapping("/confirm/{orderId}")
    public String showConfirmAndPayPage(@PathVariable("orderId") Long orderId,
                                        HttpSession session, Model model,
                                        @AuthenticationPrincipal UserDetails currentUserDetails) {

        if (currentUserDetails == null) {
            return "redirect:/login?redirect=/checkout/confirm/" + orderId;
        }

        Long pendingOrderIdFromSession = (Long) session.getAttribute("pendingOrderId");
        if (pendingOrderIdFromSession == null || !pendingOrderIdFromSession.equals(orderId)) {
            log.warn("ID ordine in sessione ({}) non corrisponde a ID ordine URL ({}) per utente {}. Reindirizzamento.",
                    pendingOrderIdFromSession, orderId, currentUserDetails.getUsername());
            // Reindirizza a un punto sicuro, come la dashboard utente o il carrello.
            // Questo previene che l'utente veda la pagina di conferma per un ordine vecchio/diverso
            // se manipola l'URL o se la sessione è cambiata.
            return "redirect:/customer/orders"; // Assumendo che esista o sarà creato
        }

        try {
            Order order = orderService.findOrderById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

            // Verifica aggiuntiva: l'ordine appartiene all'utente corrente?
            if (!order.getUser().getUsername().equals(currentUserDetails.getUsername())) {
                log.warn("Tentativo di accesso all'ordine ID {} da parte di utente non autorizzato ({} vs {})",
                        orderId, currentUserDetails.getUsername(), order.getUser().getUsername());
                return "redirect:/"; // O una pagina di errore di accesso negato
            }

            // Verifica che lo stato dell'ordine sia PENDING (o appropriato per questa fase)
            if (order.getStatus() != OrderStatus.PENDING) {
                log.warn("Tentativo di accedere alla pagina di conferma per l'ordine ID {} che non è in stato PENDING (stato attuale: {}). Utente: {}",
                        orderId, order.getStatus(), currentUserDetails.getUsername());
                // Potrebbe essere già stato pagato o cancellato. Reindirizza alla cronologia ordini.
                return "redirect:/customer/orders";
            }

            model.addAttribute("order", order);
            model.addAttribute("paypalClientId", this.paypalClientId); // Passa il Client ID PayPal

            log.info("Visualizzazione pagina conferma e pagamento per ordine ID {} utente {}", orderId, currentUserDetails.getUsername());
            return "front/confirm-payment"; // Vista JSP

        } catch (ResourceNotFoundException e) {
            log.warn("Ordine ID {} non trovato per la pagina di conferma.", orderId);
            return "redirect:/"; // O una pagina di errore 404 ordini
        }
    }

    // (Opzionale) Endpoint per annullare esplicitamente un ordine PENDING dalla pagina di conferma
    @PostMapping("/cancel-order/{orderId}")
    public String cancelPendingOrder(@PathVariable Long orderId,
                                     @AuthenticationPrincipal UserDetails currentUser,
                                     HttpSession session, // Per rimuovere pendingOrderId
                                     RedirectAttributes redirectAttributes) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.findOrderById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

            if (!order.getUser().getUsername().equals(currentUser.getUsername())) {
                log.warn("Tentativo non autorizzato di annullare l'ordine ID {} da parte dell'utente {}", orderId, currentUser.getUsername());
                redirectAttributes.addFlashAttribute("errorMessage", "Non sei autorizzato ad annullare questo ordine.");
                return "redirect:/"; // O alla cronologia ordini
            }

            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                // TODO: Considerare di rimettere a disposizione lo stock dei prodotti qui.
                // Questo richiederebbe di iterare sugli orderItems e aggiornare lo stock in ProductRepository.
                // Per ora, cambiamo solo lo stato.
                orderService.saveOrder(order); // Assicurati che OrderService abbia un metodo save/update
                session.removeAttribute("pendingOrderId"); // Rimuovi dalla sessione
                redirectAttributes.addFlashAttribute("successMessage", "Ordine #" + order.getOrderNumber() + " annullato con successo.");
                log.info("Ordine ID {} (#{}) annullato dall'utente {}", order.getId(), order.getOrderNumber(), currentUser.getUsername());
                return "redirect:/cart"; // O alla home o /customer/orders
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Impossibile annullare l'ordine perché non è più in attesa di pagamento.");
                return "redirect:/customer/orders"; // O alla pagina dettaglio ordine
            }
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ordine non trovato.");
            return "redirect:/customer/orders";
        } catch (Exception e) {
            log.error("Errore durante l'annullamento dell'ordine ID {}", orderId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'annullamento dell'ordine.");
            return "redirect:/checkout/confirm/" + orderId; // Torna alla pagina di conferma se c'è un errore imprevisto
        }
    }
}