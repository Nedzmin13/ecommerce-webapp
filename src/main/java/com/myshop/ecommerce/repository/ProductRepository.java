package com.myshop.ecommerce.repository;

import com.myshop.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // RIMUOVI O COMMENTA

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> { // RIMUOVI JpaSpecificationExecutor

    // Per visualizzare tutti i prodotti paginati (usato da admin list e fallback)
    Page<Product> findAll(Pageable pageable);

    // Per filtrare per categoria
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Per ricerca per keyword (nome O descrizione)
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword, String descriptionKeyword, Pageable pageable);

    // Per ricerca per categoria E keyword (nome O descrizione)
    Page<Product> findByCategoryIdAndNameContainingIgnoreCaseOrCategoryIdAndDescriptionContainingIgnoreCase(
            Long categoryIdForName, String nameKeyword,
            Long categoryIdForDescription, String descriptionKeyword,
            Pageable pageable
    );

    // Potremmo aggiungere metodi per il range di prezzo se vogliamo farlo a livello di repository
    // Esempio (da implementare se necessario, o gestire nel service/specifiche dopo):
    // Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    // Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    // Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPriceBetween(String kw1, String kw2, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    // ... e così via per tutte le combinazioni. Diventa complesso rapidamente.

    boolean existsByNameIgnoreCase(String name); // Utile per la validazione
}