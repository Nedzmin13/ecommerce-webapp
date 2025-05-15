package com.myshop.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
// Rimuovi import UpdateTimestamp se non hai la colonna updated_at nel tuo data.sql/tabella
// import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "category")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Manteniamo per ora
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    // Rimosso slug
    // @Column(nullable = false, length = 60, unique = true)
    // private String slug;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Aggiornato a stock_quantity e tipo Integer
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0; // Corrisponde a stock_quantity in SQL

    // Aggiornato a image_url
    @Column(name = "image_url", length = 255) // Corrisponde a image_url in SQL
    private String imageUrl;

    // Aggiunto campo available
    @Column(nullable = false)
    private boolean available = true; // Corrisponde a available in SQL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Rimuovi se non hai updated_at
    // @UpdateTimestamp
    // @Column(name = "updated_at", nullable = false)
    // private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}