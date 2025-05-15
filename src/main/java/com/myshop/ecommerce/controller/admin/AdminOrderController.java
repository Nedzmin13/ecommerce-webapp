package com.myshop.ecommerce.controller.admin;

import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.enums.OrderStatus;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.BreadcrumbItem;
import com.myshop.ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    private final OrderService orderService;

    private static final int ADMIN_ORDERS_PAGE_SIZE = 15;

    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "" + ADMIN_ORDERS_PAGE_SIZE) int size,
            @RequestParam(name = "sortField", defaultValue = "orderDate") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        log.debug("Richiesta lista tutti gli ordini admin: page={}, size={}, sortField={}, sortDir={}",
                page, size, sortField, sortDir);

        try {
            Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort pageSort = Sort.by(direction, sortField);
            Pageable pageable = PageRequest.of(page, size, pageSort);

            Page<Order> orderPage = orderService.findAllOrdersPaginated(pageable); // NUOVO METODO IN SERVICE

            model.addAttribute("orderPage", orderPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            model.addAttribute("activePage", "adminOrders");


            model.addAttribute("activePage", "adminOrders");
            // --- BREADCRUMB ---
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Ordini", null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            // --- FINE BREADCRUMB ---
            return "admin/orders/list-orders";

        } catch (Exception e) {
            log.error("Errore durante il recupero lista ordini admin", e);
            model.addAttribute("errorMessage", "Errore durante il caricamento degli ordini.");
            return "admin/orders/list-orders";
        }
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetail(@PathVariable("orderId") Long orderId, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Richiesta dettaglio ordine admin ID: {}", orderId);
        try {
            Order order = orderService.findOrderById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

            model.addAttribute("order", order);
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            model.addAttribute("orderStatusValues", OrderStatus.values());

            model.addAttribute("activePage", "adminOrders");
            List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard"));
            breadcrumbs.add(new BreadcrumbItem("Gestione Ordini", "/admin/orders"));
            breadcrumbs.add(new BreadcrumbItem("Dettaglio Ordine #" + order.getOrderNumber(), null));
            model.addAttribute("breadcrumbs", breadcrumbs);
            return "admin/orders/detail-order";

        } catch (ResourceNotFoundException ex) {
            log.warn("Ordine non trovato dall'admin con ID: {}", orderId);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/orders";
        } catch (Exception e) {
            log.error("Errore durante il caricamento del dettaglio ordine admin ID {}", orderId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante il caricamento dell'ordine.");
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/update-status/{orderId}")
    public String updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("newStatus") String newStatusString,
            RedirectAttributes redirectAttributes) {

        log.info("Richiesta aggiornamento stato per ordine ID {} al nuovo stato: {}", orderId, newStatusString);

        try {
            OrderStatus newStatus = OrderStatus.valueOf(newStatusString.toUpperCase());

            orderService.updateOrderStatus(orderId, newStatus);

            redirectAttributes.addFlashAttribute("successMessage", "Stato dell'ordine #" + orderId + " aggiornato a " + newStatus + ".");
            log.info("Stato ordine ID {} aggiornato a {}", orderId, newStatus);


        } catch (IllegalArgumentException e) {
            log.error("Stato ordine non valido fornito: {}", newStatusString, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Stato ordine non valido: " + newStatusString);
        } catch (ResourceNotFoundException e) {
            log.warn("Tentativo di aggiornare stato per ordine ID {} non trovato.", orderId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Errore durante l'aggiornamento dello stato per l'ordine ID {}: {}", orderId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento dello stato dell'ordine.");
        }

        return "redirect:/admin/orders/" + orderId;
    }
}