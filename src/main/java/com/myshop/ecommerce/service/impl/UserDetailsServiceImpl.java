package com.myshop.ecommerce.service.impl; // O com.myshop.ecommerce.security

import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // Rende questa classe un bean gestito da Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true) // Transazione per caricare i ruoli (lazy by default)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Tentativo di caricamento utente: {}", usernameOrEmail);

        // Cerca l'utente per username O per email
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail)) // Prova con l'email se l'username non è trovato
                .orElseThrow(() -> {
                    log.warn("Utente non trovato: {}", usernameOrEmail);
                    return new UsernameNotFoundException("Utente non trovato con username o email: " + usernameOrEmail);
                });

        log.info("Utente trovato: {}, Ruoli: {}", user.getUsername(), user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));

        // Mappa i ruoli dell'entità User ai GrantedAuthority di Spring Security
        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Costruisce l'oggetto UserDetails richiesto da Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),       // Usa l'username come principal
                user.getPassword(),       // Password hashata dal DB
                user.isEnabled(),         // Stato account abilitato/disabilitato
                true,                     // accountNonExpired (non gestiamo scadenza per ora)
                true,                     // credentialsNonExpired (non gestiamo scadenza credenziali)
                true,                     // accountNonLocked (non gestiamo blocco account per ora)
                authorities               // Lista dei ruoli/permessi
        );
    }



}