package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.entity.Role;
import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.AuthProvider;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.repository.RoleRepository;
import com.myshop.ecommerce.repository.UserRepository;
import com.myshop.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Importa per gestire errori di unicità username
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerNewUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Errore: Username già in uso!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Errore: Email già registrata!");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setProvider(AuthProvider.LOCAL);
        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo ROLE_CUSTOMER non trovato."));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        log.info("Registrazione nuovo utente: {}", username);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true) // Solo lettura, non modifica dati
    public boolean checkCurrentPassword(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Override
    @Transactional
    public void updateUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Solo gli utenti LOCAL possono cambiare la password in questo modo.
        // Gli utenti OAuth2 gestiscono la password tramite il provider.
        if (user.getProvider() != AuthProvider.LOCAL) {
            log.warn("Tentativo di cambiare password per utente non LOCAL (ID: {}, Provider: {}). Operazione non permessa.", userId, user.getProvider());
            throw new IllegalArgumentException("La password per gli account social non può essere cambiata qui.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        // @UpdateTimestamp dovrebbe aggiornare updatedAt
        userRepository.save(user);
        log.info("Password aggiornata per utente ID: {}", userId);
    }

    @Override
    @Transactional
    public User processOAuthPostLogin(String email, String displayNameFromProvider, String providerId,
                                      AuthProvider provider, String givenFirstNameFromProvider, String givenLastNameFromProvider) {
        log.info(">>> UserServiceImpl: processOAuthPostLogin TRIGGERED. Email: {}, displayName: {}, Provider: {} <<<", email, displayNameFromProvider, provider);

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("Utente esistente trovato tramite email {}: {}", email, user.getUsername());
            if (user.getProvider() != provider) {
                log.warn("L'utente {} (provider: {}) sta tentando il login con un nuovo provider: {}. Aggiornamento provider e providerId.",
                        user.getUsername(), email, user.getProvider(), provider);
                user.setProvider(provider);
                user.setProviderId(providerId);
                // Non cambiamo la password se l'utente esisteva già, specialmente se era LOCAL.
                // La password originale (se LOCAL) rimane, ma il login avverrà tramite OAuth.
            }
            // Aggiorna nome/cognome solo se quelli forniti da OAuth sono presenti e quelli esistenti erano vuoti/null
            if ((user.getFirstName() == null || user.getFirstName().isEmpty()) && givenFirstNameFromProvider != null && !givenFirstNameFromProvider.isEmpty()) {
                user.setFirstName(givenFirstNameFromProvider);
            }
            if ((user.getLastName() == null || user.getLastName().isEmpty()) && givenLastNameFromProvider != null && !givenLastNameFromProvider.isEmpty()) {
                user.setLastName(givenLastNameFromProvider);
            }
        } else {
            isNewUser = true;
            log.info("Nessun utente trovato con email {}. Creazione nuovo utente OAuth2.", email);
            user = new User();
            user.setEmail(email);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setEnabled(true);

            // Generazione Username
            String usernameBase = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
            if (usernameBase.isEmpty() || usernameBase.length() < 3) { // Assicura una base minima o usa un prefisso
                usernameBase = "user_" + provider.toString().toLowerCase();
            }
            String username = usernameBase;
            int count = 0;
            // Limita la lunghezza massima dell'usernameBase per lasciare spazio ai numeri
            int maxBaseLength = 45; // Esempio: 50 (max) - 2 (underscore+numero) - 3 (spazio per numeri più grandi)
            if (usernameBase.length() > maxBaseLength) {
                usernameBase = usernameBase.substring(0, maxBaseLength);
            }
            username = usernameBase; // Riassegna dopo eventuale troncamento

            while (userRepository.existsByUsername(username)) {
                count++;
                username = usernameBase + count;
                if (username.length() > 50) {
                    // Se nonostante tutto diventa troppo lungo, usa UUID. Questo è un fallback estremo.
                    username = "user_" + UUID.randomUUID().toString().substring(0, Math.min(8, 50 - "user_".length()));
                    log.warn("Username generato con UUID per evitare collisioni/lunghezza: {}", username);
                    break; // Esci dal while dopo aver generato con UUID
                }
            }
            user.setUsername(username);
            log.info("Username generato per il nuovo utente: {}", username);

            user.setFirstName(givenFirstNameFromProvider != null ? givenFirstNameFromProvider : "");
            user.setLastName(givenLastNameFromProvider != null ? givenLastNameFromProvider : "");
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Password fittizia

            Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseThrow(() -> {
                        log.error("ERRORE CRITICO: Ruolo ROLE_CUSTOMER non trovato nel database durante la registrazione OAuth2.");
                        return new RuntimeException("Configurazione ruoli errata.");
                    });
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            log.info("Nuovo utente {} ({}) creato con ruolo ROLE_CUSTOMER.", username, email);
        }

        log.info("Tentativo di salvataggio utente: Username={}, Email={}, Provider={}, ProviderId={}, Nome={}, Cognome={}",
                user.getUsername(), user.getEmail(), user.getProvider(), user.getProviderId(), user.getFirstName(), user.getLastName());

        try {
            User savedUser = userRepository.save(user); // Salva l'utente (nuovo o aggiornato)
            if(isNewUser) {
                log.info("Nuovo utente OAuth2 salvato con successo nel DB. ID Utente: {}, Username: {}", savedUser.getId(), savedUser.getUsername());
            } else {
                log.info("Utente OAuth2 esistente aggiornato con successo nel DB. ID Utente: {}, Username: {}", savedUser.getId(), savedUser.getUsername());
            }
            return savedUser;
        } catch (DataIntegrityViolationException e) { // Es. username duplicato nonostante i controlli
            log.error("!!! ERRORE DI INTEGRITA' DATI DURANTE IL SALVATAGGIO DELL'UTENTE OAUTH2 ({}) NEL DATABASE !!!: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Impossibile salvare l'utente OAuth2 a causa di un conflitto di dati (es. username duplicato): " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("!!! ERRORE CRITICO DURANTE IL SALVATAGGIO DELL'UTENTE OAUTH2 ({}) NEL DATABASE !!!: {}", user.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Impossibile salvare l'utente OAuth2 nel database: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId, String firstName, String lastName) {
        log.info("Tentativo di aggiornamento profilo per utente ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        boolean changed = false;
        if (firstName != null && !firstName.isEmpty() && !firstName.equals(user.getFirstName())) {
            user.setFirstName(firstName);
            changed = true;
        }
        if (lastName != null && !lastName.isEmpty() && !lastName.equals(user.getLastName())) {
            user.setLastName(lastName);
            changed = true;
        }

        if (changed) {
            // L'annotazione @UpdateTimestamp su User dovrebbe aggiornare updatedAt automaticamente
            User updatedUser = userRepository.save(user);
            log.info("Profilo utente ID {} aggiornato con successo. Nome: {}, Cognome: {}", userId, updatedUser.getFirstName(), updatedUser.getLastName());
            return updatedUser;
        } else {
            log.info("Nessuna modifica rilevata per il profilo utente ID {}", userId);
            return user; // Restituisce l'utente originale se non ci sono state modifiche
        }
    }



}