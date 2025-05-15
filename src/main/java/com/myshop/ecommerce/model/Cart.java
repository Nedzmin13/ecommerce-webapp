package com.myshop.ecommerce.model;

import lombok.Data;

import java.io.Serializable; // Importante se il carrello va in sessione
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data // Lombok
public class Cart implements Serializable { // Implementa Serializable per la sessione HTTP

    private static final long serialVersionUID = 1L; // Buona pratica per Serializable

    private List<CartItem> items;
    private BigDecimal totalAmount;
    private int totalItems;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.totalItems = 0;
    }

    // Metodo per aggiungere un item al carrello o aggiornare la quantità se già presente
    public void addItem(CartItem newItem) {
        Optional<CartItem> existingItemOpt = findItemByProductId(newItem.getProductId());

        if (existingItemOpt.isPresent()) {
            // Prodotto già presente, aggiorna la quantità
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            // Nuovo prodotto, aggiungilo alla lista
            this.items.add(newItem);
        }
        recalculateCartTotals();
    }

    // Metodo per aggiornare la quantità di un item specifico
    public void updateItemQuantity(Long productId, int quantity) {
        Optional<CartItem> itemOpt = findItemByProductId(productId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            if (quantity > 0) {
                item.setQuantity(quantity);
            } else {
                // Se la quantità è 0 o negativa, rimuovi l'item
                this.items.remove(item);
            }
            recalculateCartTotals();
        }
    }

    // Metodo per rimuovere un item dal carrello
    public void removeItem(Long productId) {
        this.items.removeIf(item -> item.getProductId().equals(productId));
        recalculateCartTotals();
    }

    // Metodo per svuotare il carrello
    public void clearCart() {
        this.items.clear();
        recalculateCartTotals();
    }

    // Metodo helper per trovare un item per Product ID
    public Optional<CartItem> findItemByProductId(Long productId) {
        return this.items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    // Metodo per ricalcolare i totali del carrello (quantità totale e importo totale)
    public void recalculateCartTotals() {
        this.totalItems = 0;
        this.totalAmount = BigDecimal.ZERO;
        for (CartItem item : this.items) {
            this.totalItems += item.getQuantity();
            this.totalAmount = this.totalAmount.add(item.getSubtotal());
        }
    }

    // Getter espliciti possono essere utili se non si usa Lombok o per logica custom
    // public List<CartItem> getItems() { return items; }
    // public BigDecimal getTotalAmount() { return totalAmount; }
    // public int getTotalItems() { return totalItems; }
}