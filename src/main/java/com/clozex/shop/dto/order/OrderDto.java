package com.clozex.shop.dto.order;

import com.clozex.shop.model.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long userId,
        Status status,
        BigDecimal total,
        LocalDateTime createdAt,
        String shippingAddress,
        List<OrderItemDto> orderItems
) {
}
