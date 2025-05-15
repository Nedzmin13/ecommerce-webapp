package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();

    Optional<Category> findById(Long id);


    Category createCategory(String name, String description);

    Category updateCategory(Long id, String name, String description);

    void deleteCategory(Long id);

    Category findOrCreateByName(String name, String description);
}