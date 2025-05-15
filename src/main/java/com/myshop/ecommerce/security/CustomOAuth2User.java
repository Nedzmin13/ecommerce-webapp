package com.myshop.ecommerce.security;

import com.myshop.ecommerce.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final User localUser;

    public CustomOAuth2User(OAuth2User oauth2User, User localUser) {
        this.oauth2User = oauth2User;
        this.localUser = localUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return localUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() { // Questo è l'ID/nome principale per OAuth2User
        return localUser.getUsername(); // O potresti restituire oauth2User.getName() se preferisci l'ID del provider
    }

    // --- AGGIUNGI QUESTO METODO ---
    public String getUsername() {
        return localUser.getUsername();
    }
    // -----------------------------

    public String getEmail() {
        return localUser.getEmail();
    }

    public User getLocalUser() {
        return localUser;
    }

    public String getFullName() {
        // Gestisci il caso in cui firstName o lastName potrebbero essere null o vuoti
        String fn = localUser.getFirstName() != null ? localUser.getFirstName() : "";
        String ln = localUser.getLastName() != null ? localUser.getLastName() : "";
        if (!fn.isEmpty() && !ln.isEmpty()) {
            return fn + " " + ln;
        } else if (!fn.isEmpty()) {
            return fn;
        } else if (!ln.isEmpty()) {
            return ln;
        }
        return localUser.getUsername(); // Fallback all'username se nome e cognome sono vuoti
    }
}