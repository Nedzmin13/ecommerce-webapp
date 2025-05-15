package com.myshop.ecommerce.repository;

import com.myshop.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Metodo findBySlug rimosso
    // Optional<Category> findBySlug(String slug);

    // Manteniamo findByName e existsByName per controlli
    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    // Metodo existsBySlug rimosso
    // boolean existsBySlug(String slug);
}