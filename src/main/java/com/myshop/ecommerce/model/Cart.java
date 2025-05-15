package com.myshop.ecommerce.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data // Lombok
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CartItem> items;
    private BigDecimal totalAmount;
    private int totalItems;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.totalItems = 0;
    }

    public void addItem(CartItem newItem) {
        Optional<CartItem> existingItemOpt = findItemByProductId(newItem.getProductId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            this.items.add(newItem);
        }
        recalculateCartTotals();
    }

    public void updateItemQuantity(Long productId, int quantity) {
        Optional<CartItem> itemOpt = findItemByProductId(productId);
        if (itemOpt.isPresent()) {
            CartItem item = itemOpt.get();
            if (quantity > 0) {
                item.setQuantity(quantity);
            } else {
                this.items.remove(item);
            }
            recalculateCartTotals();
        }
    }

    public void removeItem(Long productId) {
        this.items.removeIf(item -> item.getProductId().equals(productId));
        recalculateCartTotals();
    }

    public void clearCart() {
        this.items.clear();
        recalculateCartTotals();
    }

    public Optional<CartItem> findItemByProductId(Long productId) {
        return this.items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    public void recalculateCartTotals() {
        this.totalItems = 0;
        this.totalAmount = BigDecimal.ZERO;
        for (CartItem item : this.items) {
            this.totalItems += item.getQuantity();
            this.totalAmount = this.totalAmount.add(item.getSubtotal());
        }
    }


}