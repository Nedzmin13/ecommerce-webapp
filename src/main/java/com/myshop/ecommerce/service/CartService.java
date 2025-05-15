package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.model.Cart;

import javax.servlet.http.HttpSession; // Import necessario per la sessione

public interface CartService {

    /**
     * Recupera il carrello dalla sessione corrente. Se non esiste, ne crea uno nuovo.
     * @param session La sessione HTTP corrente.
     * @return L'oggetto Cart.
     */
    Cart getCart(HttpSession session);

    /**
     * Aggiunge un prodotto al carrello o ne incrementa la quantità.
     * @param productId L'ID del prodotto da aggiungere.
     * @param quantity La quantità da aggiungere.
     * @param session La sessione HTTP corrente.
     */
    void addProductToCart(Long productId, int quantity, HttpSession session);

    /**
     * Aggiorna la quantità di un prodotto nel carrello.
     * @param productId L'ID del prodotto da aggiornare.
     * @param quantity La nuova quantità.
     * @param session La sessione HTTP corrente.
     */
    void updateProductQuantityInCart(Long productId, int quantity, HttpSession session);

    /**
     * Rimuove un prodotto dal carrello.
     * @param productId L'ID del prodotto da rimuovere.
     * @param session La sessione HTTP corrente.
     */
    void removeProductFromCart(Long productId, HttpSession session);

    /**
     * Svuota il carrello.
     * @param session La sessione HTTP corrente.
     */
    void clearCart(HttpSession session);

    /**
     * Salva il carrello modificato nella sessione.
     * (Potrebbe non essere necessario come metodo pubblico se ogni operazione salva automaticamente)
     * @param session La sessione HTTP corrente.
     * @param cart L'oggetto Cart da salvare.
     */
    void saveCart(HttpSession session, Cart cart);
}