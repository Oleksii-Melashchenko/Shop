package com.clozex.shop.repository.order;

import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Page<OrderItemDto> findAllByOrderId(Long orderId, Pageable pageable);

    OrderItemDto findByOrderIdAndId(Long orderId, Long itemId);

}

