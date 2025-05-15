package com.myshop.ecommerce.repository;

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



    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
}