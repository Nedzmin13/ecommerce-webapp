package com.myshop.ecommerce.controller.admin;

import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.exception.ResourceNotFoundException; // Se la usi per categoria non trovata
import com.myshop.ecommerce.model.BreadcrumbItem;
import com.myshop.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Per nomi duplicati
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid; // Se aggiungi validazione al DTO/Entità Categoria
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private static final Logger log = LoggerFactory.getLogger(AdminCategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // --- READ: Lista Categorie ---
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.findAll(); // Potremmo volerla paginata in futuro
        model.addAttribute("categories", categories);
        model.addAttribute("activePage", "adminCategories");
        model.addAttribute("activePage", "adminCategories");

        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
        breadcrumbs.add(new BreadcrumbItem("Gestione Categorie", null));
        model.addAttribute("breadcrumbs", breadcrumbs);

        log.debug("Recuperate {} categorie per la vista admin.", categories.size());
        return "admin/categories/list-categories"; // -> /WEB-INF/views/admin/categories/list-categories.jsp
    }

    // --- CREATE: Mostra Form Nuova Categoria ---
    @GetMapping("/new")
    public String showNewCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Aggiungi Nuova Categoria");
        model.addAttribute("activePage", "adminCategories");

        // --- BREADCRUMB ---
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
        breadcrumbs.add(new BreadcrumbItem("Gestione Categorie", "/admin/categories"));
        breadcrumbs.add(new BreadcrumbItem("Aggiungi Nuova", null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        // --- FINE BREADCRUMB ---
        return "admin/categories/form-category"; // -> /WEB-INF/views/admin/categories/form-category.jsp
    }

    // --- UPDATE: Mostra Form Modifica Categoria ---
    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria non valida con Id: " + id));
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Modifica Categoria: " + category.getName());

            model.addAttribute("activePage", "adminCategories");
            // --- BREADCRUMB ---
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Categorie", "/admin/categories"));
            breadcrumbs.add(new BreadcrumbItem("Modifica: " + category.getName(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            // --- FINE BREADCRUMB ---
            return "admin/categories/form-category";
        } catch (IllegalArgumentException ex) {
            log.warn("Tentativo di modificare categoria inesistente ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/categories";
        }
    }

    // --- SAVE: Processa Creazione/Modifica Categoria ---
    @PostMapping("/save")
    public String saveCategory(
            @ModelAttribute("category") @Valid Category category, // Usa @Valid se hai annotazioni di validazione sull'entità
            BindingResult bindingResult,
            Model model, // Per ripopolare il form in caso di errore di validazione
            RedirectAttributes redirectAttributes) {

        // Se hai un DTO per la categoria con validazioni, usalo qui invece dell'entità diretta.
        // Per ora usiamo l'entità Category e assumiamo validazioni base (es. @NotBlank sul nome).
        // Se l'entità Category non ha annotazioni @Valid, rimuovi @Valid e BindingResult per ora.

        if (bindingResult.hasErrors() || model.containsAttribute("errorMessage")) {
            log.warn("Errore di validazione per la categoria: {}", bindingResult.getAllErrors());
            model.addAttribute("pageTitle", category.getId() == null ? "Aggiungi Nuova Categoria" : "Modifica Categoria: " + category.getName());
            model.addAttribute("activePage", "adminCategories");
            // --- BREADCRUMB PER FORM CON ERRORI ---
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Categorie", "/admin/categories"));
            breadcrumbs.add(new BreadcrumbItem(category.getId() == null ? "Aggiungi Nuova" : "Modifica: " + category.getName(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            // --- FINE BREADCRUMB ---
            return "admin/categories/form-category";
        }

        boolean isNew = category.getId() == null;
        String successMessage;

        try {
            if (isNew) {
                // Il tuo CategoryService.createCategory prende (String name, String description)
                categoryService.createCategory(category.getName(), category.getDescription());
                successMessage = "Categoria '" + category.getName() + "' creata con successo!";
                log.info(successMessage);
            } else {
                // Il tuo CategoryService.updateCategory prende (Long id, String name, String description)
                categoryService.updateCategory(category.getId(), category.getName(), category.getDescription());
                successMessage = "Categoria '" + category.getName() + "' aggiornata con successo!";
                log.info(successMessage);
            }
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/admin/categories";

        } catch (DataIntegrityViolationException e) { // Per nomi/slug duplicati
            log.warn("Errore di integrità dati salvando la categoria '{}': {}", category.getName(), e.getMessage());
            // bindingResult.rejectValue("name", "error.category", "Nome categoria già esistente.");
            model.addAttribute("errorMessage", "Nome categoria già esistente o altro errore di integrità.");
            model.addAttribute("pageTitle", isNew ? "Aggiungi Nuova Categoria" : "Modifica Categoria: " + category.getName());
            return "admin/categories/form-category";
        } catch (Exception e) {
            log.error("Errore generico salvando la categoria '{}': {}", category.getName(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante il salvataggio della categoria.");
            return "redirect:/admin/categories";
        }
    }

    // --- DELETE: Elimina Categoria ---
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Recupera il nome per il messaggio prima di cancellare
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria non trovata con ID: " + id));
            String categoryName = category.getName();

            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoria '" + categoryName + "' eliminata con successo!");
            log.info("Categoria ID {} ({}) eliminata.", id, categoryName);
        } catch (IllegalArgumentException ex) { // Categoria non trovata
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (DataIntegrityViolationException e) { // Se ci sono prodotti collegati e non puoi cancellare
            log.warn("Impossibile eliminare la categoria ID {}: prodotti associati.", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Impossibile eliminare la categoria: ci sono prodotti associati. Rimuovi prima i prodotti da questa categoria.");
        } catch (Exception e) {
            log.error("Errore durante l'eliminazione della categoria ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'eliminazione della categoria.");
        }
        return "redirect:/admin/categories";
    }
}