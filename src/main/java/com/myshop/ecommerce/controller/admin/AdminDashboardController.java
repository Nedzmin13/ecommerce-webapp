package com.myshop.ecommerce.controller.admin;

import com.myshop.ecommerce.model.BreadcrumbItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/admin") // Mappiamo alla radice dell'area admin per questo controller
public class AdminDashboardController {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardController.class);



    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        log.info("Accesso alla dashboard amministratore.");
        model.addAttribute("pageTitle", "Dashboard Amministratore");
        model.addAttribute("activePage", "adminDashboard"); // Per navbar

        List<BreadcrumbItem> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new BreadcrumbItem("Admin Area", "/admin/dashboard")); // Link a se stesso o alla radice admin
        breadcrumbs.add(new BreadcrumbItem("Dashboard", null)); // Pagina attuale
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "admin/dashboard";
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/dashboard";
    }
}