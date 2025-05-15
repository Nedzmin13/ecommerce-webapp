package com.myshop.ecommerce.model;

import com.myshop.ecommerce.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItem {

    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.imageUrl = product.getImageUrl();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public BigDecimal calculateSubtotal() {
        if (this.unitPrice == null || this.quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
        this.subtotal = calculateSubtotal();
    }

}