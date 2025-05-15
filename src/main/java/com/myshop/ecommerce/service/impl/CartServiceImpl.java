package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.Cart;
import com.myshop.ecommerce.model.CartItem;
import com.myshop.ecommerce.repository.ProductRepository; // Per recuperare i dettagli del prodotto
import com.myshop.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Utile se recuperiamo Product

import javax.servlet.http.HttpSession;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
    public static final String CART_SESSION_KEY = "shoppingCart"; // Chiave per memorizzare il carrello in sessione

    private final ProductRepository productRepository;

    @Autowired
    public CartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
            log.info("Nuovo carrello creato e memorizzato in sessione ID: {}", session.getId());
        }
        return cart;
    }

    @Override
    @Transactional(readOnly = true) // Necessario per caricare l'entità Product
    public void addProductToCart(Long productId, int quantity, HttpSession session) {
        if (quantity <= 0) {
            log.warn("Tentativo di aggiungere prodotto ID {} con quantità non valida: {}", productId, quantity);
            return; // Non aggiungere se la quantità non è positiva
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.isAvailable() || product.getStockQuantity() < quantity) {
            // Potremmo lanciare un'eccezione specifica qui o gestire diversamente
            log.warn("Prodotto ID {} non disponibile o stock insufficiente.", productId);
            // Per ora, non aggiungiamo e logghiamo. In un'app reale, daremmo un feedback all'utente.
            throw new IllegalArgumentException("Prodotto non disponibile o stock insufficiente.");
        }

        Cart cart = getCart(session);
        CartItem newItem = new CartItem(product, quantity); // Usa il costruttore di CartItem
        cart.addItem(newItem); // La logica di unione o aggiunta è in Cart.addItem()
        saveCart(session, cart);
        log.info("Prodotto ID {} aggiunto al carrello (quantità: {}). Sessione ID: {}", productId, quantity, session.getId());
    }

    @Override
    public void updateProductQuantityInCart(Long productId, int quantity, HttpSession session) {
        Cart cart = getCart(session);
        cart.updateItemQuantity(productId, quantity); // La logica è in Cart.updateItemQuantity()
        saveCart(session, cart);
        log.info("Quantità prodotto ID {} aggiornata a {} nel carrello. Sessione ID: {}", productId, quantity, session.getId());
    }

    @Override
    public void removeProductFromCart(Long productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId); // La logica è in Cart.removeItem()
        saveCart(session, cart);
        log.info("Prodotto ID {} rimosso dal carrello. Sessione ID: {}", productId, session.getId());
    }

    @Override
    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clearCart();
        saveCart(session, cart);
        log.info("Carrello svuotato. Sessione ID: {}", session.getId());
    }

    @Override
    public void saveCart(HttpSession session, Cart cart) {
        cart.recalculateCartTotals(); // Assicura che i totali siano aggiornati prima di salvare
        session.setAttribute(CART_SESSION_KEY, cart);
        log.debug("Carrello salvato in sessione ID: {}. Totale Items: {}, Totale Importo: {}", session.getId(), cart.getTotalItems(), cart.getTotalAmount());
    }
}