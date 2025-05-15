package com.myshop.ecommerce.service;

import com.myshop.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findById(Long id);

    Product createProduct(String name, String description, BigDecimal price, Integer stockQuantity, Long categoryId, String imageUrl, boolean available);

    Product updateProduct(Long id, String name, String description, BigDecimal price, Integer stockQuantity, Long categoryId, String newImageUrlFromForm, Boolean available);



    void deleteProduct(Long id);

    /**
     * Recupera una pagina di prodotti, opzionalmente filtrata per categoria e/o keyword.
     * L'ordinamento è gestito dal Pageable.
     *
     * @param categoryId L'ID della categoria per filtrare (può essere null).
     * @param keyword La parola chiave per la ricerca nel nome o descrizione (può essere null o vuota).
     * @param pageable L'oggetto Pageable per paginazione e ordinamento.
     * @return Una pagina di prodotti filtrati.
     */
    Page<Product> getProductsByFilter(Long categoryId, String keyword, Pageable pageable);

    List<Product> findLatestProducts(int limit); // Per i nuovi arrivi

}