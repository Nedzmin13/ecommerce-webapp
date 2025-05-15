package com.myshop.ecommerce.repository;

import com.myshop.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

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



    boolean existsByNameIgnoreCase(String name); // Utile per la validazione
}