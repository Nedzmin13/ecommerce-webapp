package com.myshop.ecommerce.controller.front;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.BreadcrumbItem;
import com.myshop.ecommerce.service.CategoryService;
import com.myshop.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    private static final int DEFAULT_PAGE_SIZE = 8;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = "name,asc") String[] sort,
            Model model) {

        log.debug("Richiesta lista prodotti: categoryId={}, keyword='{}', page={}, size={}, sort={}",
                categoryId, keyword, page, size, String.join(",", sort));

        try {
            String sortField = sort[0];
            Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            List<String> validSortFields = List.of("name", "price", "createdAt");
            if (!validSortFields.contains(sortField)) {
                sortField = "name";
            }
            Sort pageSort = Sort.by(sortDirection, sortField);
            Pageable pageable = PageRequest.of(page, size, pageSort);

            Page<Product> productPage = productService.getProductsByFilter(categoryId, keyword, pageable);

            String pageTitle = "Catalogo Prodotti";
            List<Category> categories = categoryService.findAll();
            Category currentCategory = null;

            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Home", "/"));
            breadcrumbs.add(new BreadcrumbItem("Prodotti", "/products")); // CORRETTO

            if (categoryId != null) {
                Optional<Category> categoryOpt = categoryService.findById(categoryId);
                if (categoryOpt.isPresent()) {
                    currentCategory = categoryOpt.get();
                    pageTitle = currentCategory.getName();
                    breadcrumbs.add(new BreadcrumbItem(currentCategory.getName(), null));
                } else {
                    categoryId = null;
                }
            }
            // ... (logica titolo come prima) ...

            model.addAttribute("productPage", productPage);
            model.addAttribute("categories", categories);
            // ... (altri attributi model come prima) ...
            model.addAttribute("activePage", "products"); // CORRETTO
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "front/products";

        } catch (Exception e) {
            log.error("Errore durante il recupero dei prodotti filtrati", e);
            model.addAttribute("errorMessage", "Errore durante il caricamento dei prodotti.");
            return "error";
        }
    }

    @GetMapping("/product/{productId}")
    public String productDetail(@PathVariable("productId") Long productId, Model model) {
        log.debug("Richiesta dettaglio prodotto ID: {}", productId);
        try {
            Product product = productService.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            log.info("Dettaglio Prodotto - ID: {}, Nome: {}, ImmagineURL dal DB: '{}'",
                    product.getId(), product.getName(), product.getImageUrl());

            model.addAttribute("product", product);
            model.addAttribute("activePage", "products"); // CORRETTO

            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Home", "/"));
            breadcrumbs.add(new BreadcrumbItem("Prodotti", "/products")); // CORRETTO

            if (product.getCategory() != null) {
                String categoryUrl = "/products?category=" + product.getCategory().getId(); // CORRETTO
                breadcrumbs.add(new BreadcrumbItem(product.getCategory().getName(), categoryUrl));
            }
            breadcrumbs.add(new BreadcrumbItem(product.getName(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "front/product-detail";

        } catch (ResourceNotFoundException e) {
            log.warn("Prodotto non trovato: {}", e.getMessage());
            return "error/404";
        } catch (Exception e) {
            log.error("Errore durante il recupero del dettaglio prodotto ID {}", productId, e);
            model.addAttribute("errorMessage", "Errore durante il caricamento del prodotto.");
            return "error";
        }
    }
}