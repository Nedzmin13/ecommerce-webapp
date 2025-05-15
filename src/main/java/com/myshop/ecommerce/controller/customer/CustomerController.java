package com.myshop.ecommerce.controller.customer;

import com.myshop.ecommerce.dto.UserProfileDto;
import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.BreadcrumbItem;
import com.myshop.ecommerce.repository.UserRepository;
import com.myshop.ecommerce.service.OrderService;
import com.myshop.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import com.myshop.ecommerce.dto.ChangePasswordDto;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final UserService userService;

    private static final int ORDERS_PAGE_SIZE = 10;

    @Autowired
    public CustomerController(UserRepository userRepository,
                              OrderService orderService,
                              UserService userService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails currentUserDetails, Model model) {
        if (currentUserDetails == null) return "redirect:/login";

        User user = userRepository.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUserDetails.getUsername()));

        model.addAttribute("user", user);
        if (!model.containsAttribute("userProfileDto")) {
            model.addAttribute("userProfileDto", new UserProfileDto(user.getFirstName(), user.getLastName()));
        }
        if (!model.containsAttribute("changePasswordDto")) {
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
        }
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        model.addAttribute("activePage", "customerProfile");

        // --- BREADCRUMB ---
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Home", "/"));
        breadcrumbs.add(new BreadcrumbItem("Area Cliente", "/customer/profile"));
        breadcrumbs.add(new BreadcrumbItem("Il Mio Profilo", null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        // --- FINE BREADCRUMB ---

        log.info("Visualizzazione profilo per utente: {}", currentUserDetails.getUsername());
        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("userProfileDto") @Valid UserProfileDto userProfileDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUserDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        User currentUserEntity = userRepository.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUserDetails.getUsername()));

        if (bindingResult.hasErrors()) {
            log.warn("Errore di validazione durante l'aggiornamento del profilo per {}: {}", currentUserDetails.getUsername(), bindingResult.getAllErrors());
            model.addAttribute("user", currentUserEntity);
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            if (!model.containsAttribute("changePasswordDto")) {
                model.addAttribute("changePasswordDto", new ChangePasswordDto());
            }
            model.addAttribute("activePage", "customerProfile");
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Home", "/"));
            breadcrumbs.add(new BreadcrumbItem("Area Cliente", "/customer/profile"));
            breadcrumbs.add(new BreadcrumbItem("Il Mio Profilo", null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            return "customer/profile";
        }

        try {
            this.userService.updateUserProfile(currentUserEntity.getId(), userProfileDto.getFirstName(), userProfileDto.getLastName());
            redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo!");
            log.info("Profilo per {} aggiornato.", currentUserDetails.getUsername());
        } catch (Exception e) {
            log.error("Errore durante l'aggiornamento del profilo per {}: {}", currentUserDetails.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento del profilo.");
        }

        return "redirect:/customer/profile";
    }

    @GetMapping("/orders")
    public String listOrders(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUserDetails.getUsername()));
        Pageable pageable = PageRequest.of(page, ORDERS_PAGE_SIZE, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderService.findOrdersByUserPaginated(user, pageable);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        model.addAttribute("activePage", "customerOrders");

        // --- BREADCRUMB ---
        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Home", "/"));
        breadcrumbs.add(new BreadcrumbItem("Area Cliente", "/customer/profile"));
        breadcrumbs.add(new BreadcrumbItem("I Miei Ordini", null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        // --- FINE BREADCRUMB ---

        return "customer/orders-list";
    }

    @GetMapping("/order/{orderId}")
    public String viewOrderDetail(
            @PathVariable("orderId") Long orderId,
            @AuthenticationPrincipal UserDetails currentUserDetails,
            Model model) {

        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.findOrderById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
            if (!order.getUser().getUsername().equals(currentUserDetails.getUsername())) {
                model.addAttribute("errorMessage", "Non sei autorizzato a visualizzare questo ordine.");
                return "error/403";
            }
            model.addAttribute("order", order);
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            model.addAttribute("activePage", "customerOrders");

            // --- BREADCRUMB ---
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Home", "/"));
            breadcrumbs.add(new BreadcrumbItem("Area Cliente", "/customer/profile"));
            breadcrumbs.add(new BreadcrumbItem("I Miei Ordini", "/customer/orders"));
            breadcrumbs.add(new BreadcrumbItem("Dettaglio Ordine #" + order.getOrderNumber(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            // --- FINE BREADCRUMB ---

            return "customer/order-detail";
        } catch (ResourceNotFoundException e) {
            log.warn("Ordine ID {} non trovato per l'utente {}", orderId, currentUserDetails.getUsername());
            model.addAttribute("errorMessage", "Ordine non trovato.");
            return "error/404";
        }
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @ModelAttribute("changePasswordDto") @Valid ChangePasswordDto changePasswordDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUserDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        User currentUserEntity = userRepository.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUserDetails.getUsername()));

        if (bindingResult.hasErrors() || !userService.checkCurrentPassword(currentUserEntity.getId(), changePasswordDto.getCurrentPassword()) || !changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            if (!userService.checkCurrentPassword(currentUserEntity.getId(), changePasswordDto.getCurrentPassword()) && !bindingResult.hasFieldErrors("currentPassword")) {
                bindingResult.rejectValue("currentPassword", "password.error", "Password attuale non corretta.");
            }
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword()) && !bindingResult.hasFieldErrors("confirmNewPassword")) {
                bindingResult.rejectValue("confirmNewPassword", "password.mismatch", "La nuova password e la conferma non corrispondono.");
            }
            log.warn("Errore validazione/logica cambio password per {}: {}", currentUserDetails.getUsername(), bindingResult.getAllErrors());
            model.addAttribute("user", currentUserEntity);
            model.addAttribute("userProfileDto", new UserProfileDto(currentUserEntity.getFirstName(), currentUserEntity.getLastName()));
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            model.addAttribute("showChangePasswordSection", true);
            model.addAttribute("activePage", "customerProfile");
            // --- BREADCRUMB PER FORM CON ERRORI ---
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Home", "/"));
            breadcrumbs.add(new BreadcrumbItem("Area Cliente", "/customer/profile"));
            breadcrumbs.add(new BreadcrumbItem("Il Mio Profilo", null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            // --- FINE BREADCRUMB ---
            return "customer/profile";
        }

        // 1. Verifica se la password attuale è corretta
        if (!userService.checkCurrentPassword(currentUserEntity.getId(), changePasswordDto.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "password.error", "Password attuale non corretta.");
            log.warn("Tentativo cambio password fallito per {}: password attuale errata.", currentUserDetails.getUsername());
        }

        // 2. Verifica se le nuove password corrispondono
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "password.mismatch", "La nuova password e la conferma non corrispondono.");
            log.warn("Tentativo cambio password fallito per {}: le nuove password non corrispondono.", currentUserDetails.getUsername());
        }

        // Se ci sono stati errori (password attuale o mismatch), torna al form
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUserEntity);
            model.addAttribute("userProfileDto", new UserProfileDto(currentUserEntity.getFirstName(), currentUserEntity.getLastName()));
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            model.addAttribute("showChangePasswordFormWithErrors", true); // Flag per mostrare il form password
            return "customer/profile";
        }

        // 3. Se tutto ok, aggiorna la password
        try {
            userService.updateUserPassword(currentUserEntity.getId(), changePasswordDto.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Password aggiornata con successo!");
            log.info("Password aggiornata con successo per l'utente: {}", currentUserDetails.getUsername());

        } catch (IllegalArgumentException e) {
            log.warn("Errore cambio password per {}: {}", currentUserDetails.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Errore imprevisto durante l'aggiornamento della password per {}: {}", currentUserDetails.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento della password.");
        }

        return "redirect:/customer/profile";
    }
}