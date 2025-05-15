package com.myshop.ecommerce.controller.admin;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.model.BreadcrumbItem;
import com.myshop.ecommerce.service.CategoryService;
import com.myshop.ecommerce.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private static final Logger log = LoggerFactory.getLogger(AdminProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    // Assicurati che questa cartella esista: src/main/resources/static/images/products/
    private static final String UPLOAD_DIR_PRODUCTS = "src/main/resources/static/images/products/";
    private static final int ADMIN_PRODUCTS_PAGE_SIZE = 10; // Definisci la costante se usata

    @Autowired
    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "" + ADMIN_PRODUCTS_PAGE_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String[] sortArr,
            Model model) {
        try {
            String sortField = sortArr[0];
            Sort.Direction sortDirection = (sortArr.length > 1 && sortArr[1].equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort pageSort = Sort.by(sortDirection, sortField);
            Pageable pageable = PageRequest.of(page, size, pageSort);

            // Usa getFilteredProducts se vuoi mantenere la possibilità di futuri filtri qui,
            // altrimenti crea un findAllPaginated in ProductService se non esiste.
            // Per ora, assumiamo che getFilteredProducts con tutti i filtri null funzioni.
            Page<Product> productPage = productService.getProductsByFilter(null, null, pageable);

            model.addAttribute("productPage", productPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDirection.name().toLowerCase());
            model.addAttribute("reverseSortDir", sortDirection == Sort.Direction.ASC ? "desc" : "asc");
            model.addAttribute("activePage", "adminProducts");

            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Prodotti", null));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "admin/products/list";
        } catch (Exception e) {
            log.error("Errore recupero lista prodotti admin", e);
            model.addAttribute("errorMessage", "Errore caricamento prodotti.");
            return "admin/products/list"; // o una pagina di errore
        }
    }

    @GetMapping("/new")
    public String showNewProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", "Aggiungi Nuovo Prodotto");
        model.addAttribute("activePage", "adminProducts");
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
        breadcrumbs.add(new BreadcrumbItem("Gestione Prodotti", "/admin/products"));
        breadcrumbs.add(new BreadcrumbItem("Aggiungi Nuovo", null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        return "admin/products/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Prodotto non valido con Id: " + id));
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("pageTitle", "Modifica Prodotto: " + product.getName());
            model.addAttribute("activePage", "adminProducts");
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Prodotti", "/admin/products"));
            breadcrumbs.add(new BreadcrumbItem("Modifica: " + product.getName(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            return "admin/products/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") @Valid Product product,
                              BindingResult bindingResult,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        log.debug("Tentativo salvataggio prodotto: {}, Immagine caricata: {}", product.getName(), imageFile.getOriginalFilename());

        // Mantiene l'imageUrl esistente se si modifica e non si carica un nuovo file.
        // Questo valore viene dal campo hidden <form:hidden path="imageUrl"/>
        String existingImageUrl = product.getImageUrl();

        if (bindingResult.hasErrors()) {
            log.warn("Errore di validazione durante salvataggio prodotto: {}", bindingResult.getAllErrors());
            // Ripopola i dati necessari per tornare al form
            commonFormAttributes(model, product, product.getId() == null);
            return "admin/products/form";
        }

        boolean isNew = product.getId() == null;

        if (imageFile != null && !imageFile.isEmpty()) {
            if (!isValidImageFile(imageFile)) {
                model.addAttribute("errorMessageGlobal", "Formato file immagine non valido. Usare JPG, PNG, GIF.");
                commonFormAttributes(model, product, isNew);
                return "admin/products/form";
            }
            try {
                String generatedFileName = saveUploadedFile(imageFile);
                // Se si sta modificando e c'era una vecchia immagine DIVERSA dalla nuova, cancellala
                if (!isNew && existingImageUrl != null && !existingImageUrl.isEmpty() && !existingImageUrl.equals(generatedFileName)) {
                    deleteProductImage(existingImageUrl);
                }
                product.setImageUrl(generatedFileName); // Imposta il nome della nuova immagine
                log.info("Immagine {} caricata e salvata come {}", imageFile.getOriginalFilename(), generatedFileName);
            } catch (IOException e) {
                log.error("Errore durante il salvataggio dell'immagine {}: {}", imageFile.getOriginalFilename(), e.getMessage(), e);
                model.addAttribute("errorMessageGlobal", "Errore durante l'upload dell'immagine: " + e.getMessage());
                commonFormAttributes(model, product, isNew);
                return "admin/products/form";
            }
        } else if (!isNew) {
            // Nessun nuovo file caricato, l'imageUrl è già impostato dal campo hidden
            // quindi product.getImageUrl() contiene già il valore corretto (quello vecchio)
            log.debug("Nessuna nuova immagine caricata per il prodotto ID {}. Immagine attuale: {}", product.getId(), product.getImageUrl());
        }
        // Se è un nuovo prodotto e non c'è file, product.getImageUrl() sarà null o vuoto (dal binding iniziale)

        try {
            if (isNew) {
                productService.createProduct(
                        product.getName(), product.getDescription(), product.getPrice(),
                        product.getStockQuantity(), product.getCategory().getId(),
                        product.getImageUrl(), product.isAvailable());
                redirectAttributes.addFlashAttribute("successMessage", "Prodotto '" + product.getName() + "' creato con successo!");
            } else {
                productService.updateProduct(
                        product.getId(), product.getName(), product.getDescription(), product.getPrice(),
                        product.getStockQuantity(), product.getCategory().getId(),
                        product.getImageUrl(), product.isAvailable());
                redirectAttributes.addFlashAttribute("successMessage", "Prodotto '" + product.getName() + "' aggiornato con successo!");
            }
            return "redirect:/admin/products";
        } catch (DataIntegrityViolationException e) {
            log.warn("Errore di integrità dati salvando il prodotto '{}': {}", product.getName(), e.getMessage());
            model.addAttribute("errorMessageGlobal", "Nome prodotto già esistente o altro errore di integrità.");
            commonFormAttributes(model, product, isNew);
            return "admin/products/form";
        } catch (Exception e) {
            log.error("Errore generico salvando il prodotto '{}': {}", product.getName(), e.getMessage(), e);
            model.addAttribute("errorMessageGlobal", "Errore durante il salvataggio: " + e.getMessage());
            commonFormAttributes(model, product, isNew);
            return "admin/products/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        log.warn("Tentativo eliminazione prodotto ID: {}", id);
        try {
            Product product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato con ID: " + id));
            String imageName = product.getImageUrl();

            productService.deleteProduct(id);

            if (imageName != null && !imageName.isEmpty()) {
                deleteProductImage(imageName);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Prodotto ID " + id + " eliminato con successo!");
        } catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception e) {
            log.error("Errore durante l'eliminazione del prodotto ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // Metodo helper per popolare attributi comuni del form in caso di errore
    private void commonFormAttributes(Model model, Product product, boolean isNew) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("pageTitle", isNew ? "Aggiungi Nuovo Prodotto" : "Modifica Prodotto: " + (product.getName() != null ? product.getName() : ""));
        model.addAttribute("activePage", "adminProducts");
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
        breadcrumbs.add(new BreadcrumbItem("Gestione Prodotti", "/admin/products"));
        breadcrumbs.add(new BreadcrumbItem(isNew ? "Aggiungi Nuovo" : "Modifica: " + (product.getName() != null ? product.getName() : ""), null));
        model.addAttribute("breadcrumbs", breadcrumbs);
    }

    // Metodo helper per salvare il file
    private String saveUploadedFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i);
        }
        String generatedFileName = UUID.randomUUID().toString() + fileExtension;

        Path uploadPath = Paths.get(UPLOAD_DIR_PRODUCTS);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // Crea la directory se non esiste
        }
        Path filePath = uploadPath.resolve(generatedFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return generatedFileName;
    }

    private boolean isValidImageFile(MultipartFile imageFile) {
        String contentType = imageFile.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif"));
    }

    private void deleteProductImage(String imageName) {
        if (imageName == null || imageName.isEmpty()) return;
        try {
            Path imagePath = Paths.get(UPLOAD_DIR_PRODUCTS).resolve(imageName);
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                log.info("Immagine prodotto cancellata: {}", imageName);
            } else {
                log.warn("Tentativo di cancellare immagine prodotto non esistente: {}", imageName);
            }
        } catch (IOException e) {
            log.error("Errore durante la cancellazione dell'immagine prodotto {}: {}", imageName, e.getMessage());
        }
    }
}