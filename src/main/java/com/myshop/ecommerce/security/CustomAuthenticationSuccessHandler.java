package com.myshop.ecommerce.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    public CustomAuthenticationSuccessHandler() {
        // Imposta una pagina di default se non c'è una richiesta salvata
        // o se vuoi sempre reindirizzare qui dopo il login.
        setDefaultTargetUrl("/home?loginSuccess"); // Pagina di default dopo login
        setAlwaysUseDefaultTargetUrl(false); // Se true, ignora la "saved request" e usa sempre defaultTargetUrl
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        log.info("Login riuscito per l'utente: {}", username);

        // Qui potresti aggiungere logica custom, ad esempio:
        // - Caricare informazioni aggiuntive dell'utente in sessione.
        // - Reindirizzare a pagine diverse in base al ruolo.
        // - Controllare se è il primo login OAuth2 e reindirizzare a una pagina di benvenuto/completamento profilo.

        // Se un utente OAuth2 si è appena registrato tramite CustomOAuth2UserService,
        // l'oggetto 'authentication.getPrincipal()' sarà il nostro CustomOAuth2User.
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
            log.info("Utente OAuth2 {} ({}) loggato con successo.", oauth2User.getName(), oauth2User.getEmail());
            // Potresti voler impostare un target URL specifico per il primo login OAuth2
            // setDefaultTargetUrl("/customer/welcome"); // Esempio
        }

        // La logica di SavedRequestAwareAuthenticationSuccessHandler gestirà il redirect
        // alla pagina originariamente richiesta o a defaultTargetUrl.
        super.onAuthenticationSuccess(request, response, authentication);
    }
}