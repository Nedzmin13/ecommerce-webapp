package com.myshop.ecommerce.service;

import com.myshop.ecommerce.dto.ShippingAddressDto;
import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.User;
import com.myshop.ecommerce.enums.OrderStatus;
import com.myshop.ecommerce.model.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Rimuovi 'import java.util.List;' se non è più usato da nessun altro metodo
import java.util.Optional;

public interface OrderService {

    /**
     * Crea un nuovo ordine a partire dal carrello, utente e indirizzo di spedizione.
     * L'ordine viene creato con uno stato iniziale (es. PENDING).
     *
     * @param cart Il carrello dell'utente.
     * @param user L'utente che effettua l'ordine.
     * @param shippingAddressDto I dati dell'indirizzo di spedizione.
     * @return L'ordine creato e salvato.
     */
    Order createOrder(Cart cart, User user, ShippingAddressDto shippingAddressDto);

    /**
     * Trova un ordine per il suo ID.
     * @param orderId L'ID dell'ordine.
     * @return Un Optional contenente l'ordine se trovato.
     */
    Optional<Order> findOrderById(Long orderId);

    /**
     * Trova gli ordini di un utente specifico con paginazione e ordinamento.
     * @param user L'utente.
     * @param pageable L'oggetto Pageable per paginazione e ordinamento.
     * @return Una Pagina degli ordini dell'utente.
     */
    Page<Order> findOrdersByUserPaginated(User user, Pageable pageable); // Questo è il metodo corretto

    // Questo metodo è usato da PayPalController per salvare lo stato aggiornato e il pagamento
    void saveOrder(Order order);

    Page<Order> findAllOrdersPaginated(Pageable pageable);
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);

    // --- RIMUOVI O COMMENTA QUESTA RIGA ---
    // List<Order> findOrdersByUser(User user);
    // ---------------------------------------
}