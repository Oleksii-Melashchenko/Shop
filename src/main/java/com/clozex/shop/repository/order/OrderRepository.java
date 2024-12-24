package com.clozex.shop.repository.order;

import com.clozex.shop.model.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(Long id, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, Long id);
}
