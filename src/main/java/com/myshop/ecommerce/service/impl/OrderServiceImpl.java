package com.myshop.ecommerce.service.impl;

import com.myshop.ecommerce.dto.ShippingAddressDto;
import com.myshop.ecommerce.entity.*;
import com.myshop.ecommerce.enums.OrderStatus;
import com.myshop.ecommerce.exception.ResourceNotFoundException;
import com.myshop.ecommerce.model.Cart;
import com.myshop.ecommerce.model.CartItem;
import com.myshop.ecommerce.repository.*;
import com.myshop.ecommerce.service.EmailService; // Assicurati che questo import sia presente
import com.myshop.ecommerce.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService; // Campo per EmailService

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            EmailService emailService) { // Iniezione nel costruttore
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.emailService = emailService; // Assegnazione
    }

    @Override
    @Transactional
    public Order createOrder(Cart cart, User user, ShippingAddressDto shippingAddressDto) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello non può essere vuoto per creare un ordine.");
        }
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderNumber("ORD-" + LocalDateTime.now().toLocalDate() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItem.getProductId()));
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                log.warn("Stock insufficiente per il prodotto ID {} (Nome: {}) durante la creazione dell'ordine.", product.getId(), product.getName());
                throw new IllegalStateException("Stock insufficiente per il prodotto: " + product.getName());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerUnit(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getSubtotal());
            order.addOrderItem(orderItem);
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Shipping shipping = new Shipping();
        shipping.setAddressLine1(shippingAddressDto.getAddressLine1());
        shipping.setAddressLine2(shippingAddressDto.getAddressLine2());
        shipping.setCity(shippingAddressDto.getCity());
        shipping.setState(shippingAddressDto.getState());
        shipping.setPostalCode(shippingAddressDto.getPostalCode());
        shipping.setCountry(shippingAddressDto.getCountry());
        shipping.setPhone(shippingAddressDto.getPhone());
        order.setShipping(shipping);

        Order savedOrder = orderRepository.save(order);
        log.info("Ordine ID {} creato con successo per l'utente {}. Numero Ordine: {}", savedOrder.getId(), user.getUsername(), savedOrder.getOrderNumber());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Forza l'inizializzazione delle collezioni/oggetti lazy
            order.getOrderItems().size();
            if (order.getShipping() != null) order.getShipping().getAddressLine1();
            if (order.getPayment() != null) order.getPayment().getTransactionId();
            if (order.getUser() != null) order.getUser().getUsername();
            for(OrderItem item : order.getOrderItems()){
                if(item.getProduct() != null) item.getProduct().getName();
            }
        }
        return orderOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findOrdersByUserPaginated(User user, Pageable pageable) {
        log.debug("Recupero ordini paginati per utente: {}, Pageable: {}", user.getUsername(), pageable);
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional
    public void saveOrder(Order order) {
        orderRepository.save(order);
        log.info("Ordine ID {} salvato/aggiornato.", order.getId());
    }

    @Override
    @Transactional(readOnly = true) // Modificato per coerenza con JpaRepository se non ci sono modifiche
    public Page<Order> findAllOrdersPaginated(Pageable pageable) {
        log.debug("Recupero tutti gli ordini paginati. Pageable: {}", pageable);
        return orderRepository.findAll(pageable);
    }

    // --- METODO updateOrderStatus COMPLETO CON INVIO EMAIL ---
    @Override
    @Transactional // Operazione di aggiornamento, quindi transazionale
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.debug("Tentativo di aggiornare lo stato dell'ordine ID {} a {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus oldStatus = order.getStatus();
        log.info("Ordine ID {}: cambio stato da {} a {}", orderId, oldStatus, newStatus);
        order.setStatus(newStatus);
        // order.setUpdatedAt(LocalDateTime.now()); // @UpdateTimestamp in Order entity dovrebbe gestirlo automaticamente al save

        Order updatedOrder = orderRepository.save(order); // Salva l'ordine con il nuovo stato

        // Invia email di notifica se lo stato è cambiato
        if (oldStatus != newStatus) {
            try {
                // Ricarichiamo l'ordine per assicurarci che tutte le associazioni
                // necessarie per l'email siano caricate, specialmente se lazy.
                // Il nostro findOrderById già fa questo "eager fetching" manuale.
                Order orderForEmail = this.findOrderById(updatedOrder.getId())
                        .orElse(updatedOrder); // Fallback all'ordine già in memoria se non trovato (improbabile)

                emailService.sendOrderStatusUpdateEmail(orderForEmail);
                log.info("Email di aggiornamento stato inviata per l'ordine ID {}", updatedOrder.getId());
            } catch (Exception e) {
                log.error("Fallito invio email di aggiornamento stato per l'ordine ID {}: {}", updatedOrder.getId(), e.getMessage(), e);
                // Non far fallire la transazione principale per un errore di invio email
                // Considerare di mettere l'invio email in una coda o un processo asincrono in un'app reale.
            }
        }
        return updatedOrder;
    }
}