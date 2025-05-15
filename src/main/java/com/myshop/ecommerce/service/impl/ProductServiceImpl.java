package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.repository.CategoryRepository;
import com.myshop.ecommerce.repository.ProductRepository;
import com.myshop.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent() && productOpt.get().getCategory() != null) {
            productOpt.get().getCategory().getName(); // Eager fetch
        }
        return productOpt;
    }

    @Override
    @Transactional
    public Product createProduct(String name, String description, BigDecimal price, Integer stockQuantity, Long categoryId, String imageUrl, boolean available) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        if (productRepository.existsByNameIgnoreCase(name)) {
            throw new DataIntegrityViolationException("Un prodotto con il nome '" + name + "' esiste già.");
        }
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);
        product.setCategory(category);
        product.setImageUrl(imageUrl == null || imageUrl.trim().isEmpty() ? null : imageUrl.trim());
        product.setAvailable(available);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, String name, String description, BigDecimal price,
                                 Integer stockQuantity, Long categoryId, String newImageUrlFromForm, Boolean available) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        if (!productToUpdate.getName().equalsIgnoreCase(name) && productRepository.existsByNameIgnoreCase(name)) {
            Optional<Product> existingByName = productRepository.findAll().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
            if(existingByName.isPresent() && !existingByName.get().getId().equals(id)){
                throw new DataIntegrityViolationException("Un altro prodotto con il nome '" + name + "' esiste già.");
            }
        }
        productToUpdate.setName(name);
        productToUpdate.setDescription(description);
        productToUpdate.setPrice(price);
        if (stockQuantity != null) productToUpdate.setStockQuantity(stockQuantity);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            productToUpdate.setCategory(category);
        }
        if (available != null) productToUpdate.setAvailable(available);
        if (newImageUrlFromForm != null) {
            productToUpdate.setImageUrl(newImageUrlFromForm.trim().isEmpty() ? null : newImageUrlFromForm.trim());
        }
        return productRepository.save(productToUpdate);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        // Eager fetch categories
        if (productPage.hasContent()) {
            productPage.getContent().forEach(product -> {
                if (product.getCategory() != null) product.getCategory().getName();
            });
        }
        return productPage.getContent();
    }

    // IMPLEMENTAZIONE DEL NUOVO METODO UNIFICATO
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByFilter(Long categoryId, String keyword, Pageable pageable) {
        log.debug("Recupero prodotti: categoryId={}, keyword='{}', pageable={}", categoryId, keyword, pageable);
        Page<Product> productPage;
        boolean hasKeyword = StringUtils.hasText(keyword);

        if (categoryId != null) {
            if (!categoryRepository.existsById(categoryId)) {
                log.warn("Categoria ID {} non trovata per il filtro, restituisco pagina vuota.", categoryId);
                return Page.empty(pageable);
            }
            if (hasKeyword) {
                log.debug("Ricerca per categoria {} E keyword '{}'", categoryId, keyword);
                productPage = productRepository.findByCategoryIdAndNameContainingIgnoreCaseOrCategoryIdAndDescriptionContainingIgnoreCase(
                        categoryId, keyword, categoryId, keyword, pageable);
            } else {
                log.debug("Ricerca per categoria {}", categoryId);
                productPage = productRepository.findByCategoryId(categoryId, pageable);
            }
        } else {
            if (hasKeyword) {
                log.debug("Ricerca per keyword '{}'", keyword);
                productPage = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
            } else {
                log.debug("Recupero tutti i prodotti paginati");
                productPage = productRepository.findAll(pageable);
            }
        }

        // Eager fetch categories per la pagina corrente
        if (productPage.hasContent()) {
            productPage.getContent().forEach(product -> {
                if (product.getCategory() != null) {
                    product.getCategory().getName();
                }
            });
        }
        return productPage;
    }
}