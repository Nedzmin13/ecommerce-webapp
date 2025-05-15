package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.repository.CategoryRepository;
import com.myshop.ecommerce.service.CategoryService;
// Rimuovi import SlugUtil
// import com.myshop.ecommerce.util.SlugUtil;
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

    // Metodo findBySlug rimosso

    @Override
    @Transactional
    public Category createCategory(String name, String description) {
        // Rimuovi logica slug
        // String slug = SlugUtil.toSlug(name);
        // if (categoryRepository.existsBySlug(slug)) {
        //     throw new IllegalArgumentException("Errore: Una categoria con slug simile esiste già.");
        // }
        if (categoryRepository.existsByName(name)) {
            log.warn("Tentativo di creare categoria con nome duplicato: {}", name);
            throw new DataIntegrityViolationException("Errore: Una categoria con il nome '" + name + "' esiste già.");
        }

        Category category = new Category(name, description); // Usa nuovo costruttore
        // category.setSlug(slug); // Rimosso
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

        // Verifica se il nuovo nome è già usato da ALTRE categorie
        Optional<Category> existingByName = categoryRepository.findByName(name);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            log.warn("Tentativo di aggiornare categoria ID {} con nome duplicato: {}", id, name);
            throw new DataIntegrityViolationException("Errore: Nome categoria già in uso da un'altra categoria.");
        }
        // Rimuovi controllo slug
        // String newSlug = SlugUtil.toSlug(name);
        // Optional<Category> existingBySlug = categoryRepository.findBySlug(newSlug);
        // if (existingBySlug.isPresent() && !existingBySlug.get().getId().equals(id)) {
        //     throw new IllegalArgumentException("Errore: Slug generato già in uso.");
        // }

        category.setName(name);
        category.setDescription(description); // Aggiorna descrizione
        // category.setSlug(newSlug); // Rimosso

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
        // La logica della cancellazione a cascata dei prodotti rimane invariata
        log.warn("Cancellazione categoria ID {} ('{}') e dei relativi prodotti (a causa della cascata)", id, category.getName());
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Category findOrCreateByName(String name, String description) {
        Optional<Category> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            // Potremmo voler aggiornare la descrizione se quella fornita è diversa? Per ora non lo facciamo.
            return existingCategory.get();
        } else {
            // Crea la categoria se non esiste
            return createCategory(name, description);
        }
    }
}