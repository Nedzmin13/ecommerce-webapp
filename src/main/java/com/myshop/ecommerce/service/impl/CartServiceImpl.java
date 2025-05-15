package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.Cart;
import com.myshop.ecommerce.model.CartItem;
import com.myshop.ecommerce.repository.ProductRepository;
import com.myshop.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
    public static final String CART_SESSION_KEY = "shoppingCart";

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
    @Transactional(readOnly = true)
    public void addProductToCart(Long productId, int quantity, HttpSession session) {
        if (quantity <= 0) {
            log.warn("Tentativo di aggiungere prodotto ID {} con quantità non valida: {}", productId, quantity);
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.isAvailable() || product.getStockQuantity() < quantity) {
            log.warn("Prodotto ID {} non disponibile o stock insufficiente.", productId);
            throw new IllegalArgumentException("Prodotto non disponibile o stock insufficiente.");
        }

        Cart cart = getCart(session);
        CartItem newItem = new CartItem(product, quantity);
        cart.addItem(newItem);
        saveCart(session, cart);
        log.info("Prodotto ID {} aggiunto al carrello (quantità: {}). Sessione ID: {}", productId, quantity, session.getId());
    }

    @Override
    public void updateProductQuantityInCart(Long productId, int quantity, HttpSession session) {
        Cart cart = getCart(session);
        cart.updateItemQuantity(productId, quantity);
        saveCart(session, cart);
        log.info("Quantità prodotto ID {} aggiornata a {} nel carrello. Sessione ID: {}", productId, quantity, session.getId());
    }

    @Override
    public void removeProductFromCart(Long productId, HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(productId);
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
        cart.recalculateCartTotals();
        session.setAttribute(CART_SESSION_KEY, cart);
        log.debug("Carrello salvato in sessione ID: {}. Totale Items: {}, Totale Importo: {}", session.getId(), cart.getTotalItems(), cart.getTotalAmount());
    }
}