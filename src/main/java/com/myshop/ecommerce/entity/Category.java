package com.myshop.ecommerce.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "stati/images/products")
public class Category {

    @Id
    // Non mettiamo @GeneratedValue se gli ID sono specificati in data.sql
    // e vogliamo che Hibernate li usi. Hibernate è abbastanza intelligente
    // da usare gli ID forniti se non imposti una strategia di generazione esplicita
    // o se la strategia lo permette. Per sicurezza, potremmo rimuovere @GeneratedValue
    // SE E SOLO SE gli ID saranno SEMPRE forniti manualmente o da data.sql.
    // Manteniamo IDENTITY per ora, data.sql con INSERT IGNORE dovrebbe funzionare.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(length = 255) // Aggiunta colonna descrizione come nel tuo SQL
    private String description;

    // Rimuoviamo lo slug per ora
    // @Column(nullable = false, length = 60, unique = true)
    // private String slug;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Product> products;

    // Costruttore aggiornato (opzionale)
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}