package com.myshop.ecommerce.model;

import com.myshop.ecommerce.entity.Product; // Importa l'entità Product
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Lombok per getter, setter, ecc.
@NoArgsConstructor
public class CartItem {

    private Long productId; // Usiamo solo l'ID per identificare il prodotto nel carrello
    private String productName;
    private String imageUrl; // Per visualizzare nel carrello
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    // Costruttore utile quando si aggiunge un prodotto
    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.imageUrl = product.getImageUrl(); // Assumendo che imageUrl sia nell'entità Product
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    // Metodo per calcolare il subtotale
    public BigDecimal calculateSubtotal() {
        if (this.unitPrice == null || this.quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    // Metodo per aggiornare la quantità e ricalcolare il subtotale
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity); // Assicura che la quantità non sia negativa
        this.subtotal = calculateSubtotal();
    }

    // Potremmo aggiungere altri metodi helper se necessario
}