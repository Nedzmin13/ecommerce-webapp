package com.myshop.ecommerce.repository;

// ... altri import ...
import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    // Rimuovi o commenta il vecchio metodo se ne avevi uno simile
    // Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);

    /**
     * Trova tutti gli ordini per un dato utente, con paginazione e ordinamento.
     * Spring Data JPA costruirà la query basandosi sul nome del metodo.
     * L'ordinamento viene specificato nell'oggetto Pageable.
     * @param user L'utente.
     * @param pageable Oggetto per paginazione e ordinamento.
     * @return Una pagina di ordini.
     */
    Page<Order> findByUser(User user, Pageable pageable); // NUOVO o MODIFICATO

    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
}