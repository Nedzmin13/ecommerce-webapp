-- Inserimento Ruoli Base (DECOMMENTA E USA INSERT IGNORE)
INSERT IGNORE INTO roles (name) VALUES ('ROLE_CUSTOMER');
INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN');

-- Inserimento Utente Amministratore (DECOMMENTA E USA PASSWORD VALIDA)
INSERT IGNORE INTO users (username, email, password, first_name, last_name, enabled, provider, created_at, updated_at)
VALUES ('admin', 'admin@myshop.com', '$2a$10$Ze6waeqL9ELhSNcM3AqVpupQUbclaTUBPsmy1KnwLaJ8lMYeGi.Eq', 'Admin', 'User', TRUE, 'LOCAL', NOW(), NOW());

-- Associazione Ruolo ADMIN all'utente admin (DECOMMENTA)
INSERT IGNORE INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM users_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);