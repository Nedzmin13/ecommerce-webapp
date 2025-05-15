package com.myshop.ecommerce.controller.front;

import com.myshop.ecommerce.dto.UserRegistrationDto;
import com.myshop.ecommerce.entity.Category;
import com.myshop.ecommerce.entity.Product;
import com.myshop.ecommerce.service.CategoryService;
import com.myshop.ecommerce.service.EmailService;
import com.myshop.ecommerce.service.ProductService;
import com.myshop.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final UserService userService;
    private final EmailService emailService;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public HomeController(UserService userService, EmailService emailService, CategoryService categoryService, ProductService productService) {
        this.userService = userService;
        this.emailService = emailService;
        this.categoryService = categoryService;
        this.productService = productService;

    }


    // ... metodi home() e loginPage() ...
    @GetMapping(value = {"/", "/home"})
    public String home(Model model) {
        model.addAttribute("activePage", "home");

        List<Category> categories = categoryService.findAll();
        if (categories.size() > 4) {
            model.addAttribute("featuredCategories", categories.subList(0, 4));
        } else {
            model.addAttribute("featuredCategories", categories);
        }

        // Ora productService dovrebbe essere risolto
        List<Product> latestProducts = this.productService.findLatestProducts(8); // Es. mostra gli ultimi 8
        model.addAttribute("latestProducts", latestProducts);

        log.info("Accesso alla homepage");
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("activePage", "login");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        model.addAttribute("activePage", "register");
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @ModelAttribute("userDto") @Valid UserRegistrationDto userDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {
        log.info("Tentativo di registrazione per l'utente: {}", userDto.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Errore di validazione DTO: {}", bindingResult.getAllErrors());
            return "auth/register";
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userDto", "Le password non corrispondono");
            log.warn("Errore registrazione: Le password non corrispondono per {}", userDto.getUsername());
            return "auth/register";
        }
        if (userService.existsByUsername(userDto.getUsername())) {
            bindingResult.rejectValue("username", "error.userDto", "Username già in uso");
            log.warn("Errore registrazione: Username {} già esistente", userDto.getUsername());
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            bindingResult.rejectValue("email", "error.userDto", "Email già registrata");
            log.warn("Errore registrazione: Email {} già esistente", userDto.getEmail());
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerNewUser(
                    userDto.getUsername(),
                    userDto.getEmail(),
                    userDto.getPassword(),
                    userDto.getFirstName(),
                    userDto.getLastName()
            );
            log.info("Utente {} registrato con successo.", userDto.getUsername());

            String siteUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(request.getContextPath())
                    .build()
                    .toUriString();

            try {
                emailService.sendRegistrationConfirmationEmail(userDto.getEmail(), userDto.getUsername(), siteUrl);
                log.info("Email di conferma inviata a {}", userDto.getEmail());
            } catch (Exception e) {
                log.error("Fallito invio email di conferma a {}: {}", userDto.getEmail(), e.getMessage());
            }

            redirectAttributes.addFlashAttribute("registrationSuccess",
                    "Registrazione avvenuta con successo! Controlla la tua email per la conferma. Effettua il login.");
            return "redirect:/login?registered";

        } catch (Exception e) {
            log.error("Errore durante la registrazione di {}", userDto.getUsername(), e);
            model.addAttribute("errorMessage", "Errore durante la registrazione. Riprova più tardi.");
            model.addAttribute("userDto", userDto);
            return "auth/register";
        }
    }
}