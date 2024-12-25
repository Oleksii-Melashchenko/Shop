package com.clozex.shop.repository.order;

import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    OrderItemDto findByOrderIdAndId(Long orderId, Long itemId);
}
