package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.repository.CategoryRepository;
import com.myshop.ecommerce.service.CategoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }


    @Override
    @Transactional
    public Category createCategory(String name, String description) {

        if (categoryRepository.existsByName(name)) {
            log.warn("Tentativo di creare categoria con nome duplicato: {}", name);
            throw new DataIntegrityViolationException("Errore: Una categoria con il nome '" + name + "' esiste già.");
        }

        Category category = new Category(name, description);
        log.info("Creazione nuova categoria: {}", name);
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Errore di integrità durante la creazione della categoria {}: {}", name, e.getMessage());
            throw new DataIntegrityViolationException("Errore durante la creazione della categoria: " + e.getMostSpecificCause().getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        Optional<Category> existingByName = categoryRepository.findByName(name);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            log.warn("Tentativo di aggiornare categoria ID {} con nome duplicato: {}", id, name);
            throw new DataIntegrityViolationException("Errore: Nome categoria già in uso da un'altra categoria.");
        }


        category.setName(name);
        category.setDescription(description);

        log.info("Aggiornamento categoria ID {}: nuovo nome {}", id, name);
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Errore di integrità durante l'aggiornamento della categoria ID {}: {}", id, e.getMessage());
            throw new DataIntegrityViolationException("Errore durante l'aggiornamento della categoria: " + e.getMostSpecificCause().getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        log.warn("Cancellazione categoria ID {} ('{}') e dei relativi prodotti (a causa della cascata)", id, category.getName());
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Category findOrCreateByName(String name, String description) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        } else {
            return createCategory(name, description);
        }
    }
}