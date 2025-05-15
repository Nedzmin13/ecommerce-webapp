package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.AuthProvider;
import com.myshop.ecommerce.security.CustomOAuth2User; // Creeremo questa interfaccia/classe wrapper
import com.myshop.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserService userService; // Il nostro UserService esistente

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info(">>> CustomOAuth2UserService: loadUser TRIGGERED <<<"); // LOG DI DEBUG
        OAuth2User oauth2User = super.loadUser(userRequest);
        log.debug("OAuth2User attributes: {}", oauth2User.getAttributes());

        // Ottieni il provider (es. "google")
        String providerRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider authProvider = AuthProvider.valueOf(providerRegistrationId.toUpperCase());

        // Estrai le informazioni dell'utente da Google
        Map<String, Object> attributes = oauth2User.getAttributes();
        String providerSpecificId = attributes.get("sub").toString(); // "sub" è l'ID univoco di Google
        String email = attributes.get("email").toString();
        String name = attributes.get("name") != null ? attributes.get("name").toString() : email; // Fallback a email per il nome
        String firstName = attributes.get("given_name") != null ? attributes.get("given_name").toString() : "";
        String lastName = attributes.get("family_name") != null ? attributes.get("family_name").toString() : "";
        // String pictureUrl = attributes.get("picture") != null ? attributes.get("picture").toString() : null;

        // Processa l'utente: cerca nel DB, crea se non esiste, aggiorna se necessario
        User user = userService.processOAuthPostLogin(
                email,
                name, // Potremmo usare l'email o un nome generato se 'name' non è disponibile
                providerSpecificId,
                authProvider,
                firstName,
                lastName
        );

        // Restituisci un wrapper CustomOAuth2User che implementa OAuth2User e UserDetails (opzionale ma pulito)
        // Per ora, passiamo direttamente gli attributi e le autorità dal nostro utente.
        // Il nostro UserDetailsServiceImpl già gestisce la creazione di UserDetails.
        // Qui dobbiamo solo assicurare che l'utente sia nel nostro DB e poi Spring Security
        // userà UserDetailsServiceImpl per il login successivo se necessario, o semplicemente userà questo OAuth2User.
        // Creeremo un wrapper semplice per passare il nostro oggetto User.
        return new CustomOAuth2User(oauth2User, user); // Passiamo l'oauth2User originale e il nostro User
    }
}