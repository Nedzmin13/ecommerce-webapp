package com.myshop.ecommerce.controller.front;

import com.myshop.ecommerce.model.Cart;
import com.myshop.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest; // Per accedere alla sessione
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart") // Prefisso per tutte le mappature del carrello
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // --- Visualizza Pagina Carrello (GET /cart) ---
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        log.debug("Visualizzazione carrello. Sessione ID: {}, Items: {}", session.getId(), cart.getTotalItems());
        return "front/cart"; // -> /WEB-INF/views/front/cart.jsp
    }

    // --- Aggiungi Prodotto al Carrello (POST /cart/add) ---
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) { // Per ottenere l'URL di provenienza

        log.info("Richiesta aggiunta prodotto ID {} (quantità: {}) al carrello. Sessione ID: {}", productId, quantity, session.getId());
        try {
            cartService.addProductToCart(productId, quantity, session);
            redirectAttributes.addFlashAttribute("cartSuccessMessage", "Prodotto aggiunto al carrello!");
        } catch (IllegalArgumentException e) {
            log.warn("Errore durante l'aggiunta al carrello: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("cartErrorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Errore imprevisto durante l'aggiunta al carrello del prodotto ID {}", productId, e);
            redirectAttributes.addFlashAttribute("cartErrorMessage", "Errore durante l'aggiunta del prodotto al carrello.");
        }

        // Reindirizza alla pagina da cui è partita la richiesta (se disponibile) o alla pagina prodotti
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            log.debug("Reindirizzamento a Referer: {}", referer);
            return "redirect:" + referer;
        } else {
            log.debug("Referer non disponibile, reindirizzamento a /products");
            return "redirect:/products"; // Fallback
        }
    }

    // --- Aggiorna Quantità Prodotto (POST /cart/update) ---
    @PostMapping("/update")
    public String updateCartItem(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        log.info("Richiesta aggiornamento quantità prodotto ID {} a {} nel carrello. Sessione ID: {}", productId, quantity, session.getId());
        try {
            if (quantity < 0) { // Impedisci quantità negative qui
                throw new IllegalArgumentException("La quantità non può essere negativa.");
            }
            cartService.updateProductQuantityInCart(productId, quantity, session);
            redirectAttributes.addFlashAttribute("cartSuccessMessage", "Carrello aggiornato con successo.");
        } catch (IllegalArgumentException e) {
            log.warn("Errore durante l'aggiornamento del carrello: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("cartErrorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Errore imprevisto durante l'aggiornamento del carrello per il prodotto ID {}", productId, e);
            redirectAttributes.addFlashAttribute("cartErrorMessage", "Errore durante l'aggiornamento del carrello.");
        }
        return "redirect:/cart"; // Torna sempre alla pagina del carrello dopo l'aggiornamento
    }

    // --- Rimuovi Prodotto dal Carrello (POST /cart/remove/{productId}) ---
    // Usiamo POST anche per la rimozione per coerenza e per evitare CSRF se fosse un link GET
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        log.info("Richiesta rimozione prodotto ID {} dal carrello. Sessione ID: {}", productId, session.getId());
        try {
            cartService.removeProductFromCart(productId, session);
            redirectAttributes.addFlashAttribute("cartSuccessMessage", "Prodotto rimosso dal carrello.");
        } catch (Exception e) {
            log.error("Errore imprevisto durante la rimozione del prodotto ID {} dal carrello", productId, e);
            redirectAttributes.addFlashAttribute("cartErrorMessage", "Errore durante la rimozione del prodotto.");
        }
        return "redirect:/cart"; // Torna sempre alla pagina del carrello
    }

    // --- Svuota Carrello (POST /cart/clear) ---
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("Richiesta svuotamento carrello. Sessione ID: {}", session.getId());
        try {
            cartService.clearCart(session);
            redirectAttributes.addFlashAttribute("cartSuccessMessage", "Il carrello è stato svuotato.");
        } catch (Exception e) {
            log.error("Errore imprevisto durante lo svuotamento del carrello", e);
            redirectAttributes.addFlashAttribute("cartErrorMessage", "Errore durante lo svuotamento del carrello.");
        }
        return "redirect:/cart";
    }

}