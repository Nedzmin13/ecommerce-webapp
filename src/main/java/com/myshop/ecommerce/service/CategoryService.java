package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();

    Optional<Category> findById(Long id);

    // Metodo findBySlug rimosso
    // Optional<Category> findBySlug(String slug);

    // Firma aggiornata: aggiungi description
    Category createCategory(String name, String description);

    // Firma aggiornata: aggiungi description
    Category updateCategory(Long id, String name, String description);

    void deleteCategory(Long id);

    // Firma aggiornata: aggiungi description (o rendilo opzionale)
    Category findOrCreateByName(String name, String description); // Potremmo passare null per description se non sempre necessaria
}