package com.clozex.shop.service;

import com.clozex.shop.dto.order.CreateOrderDto;
import com.clozex.shop.dto.order.OrderDto;
import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.dto.order.UpdateOrderStatusDto;
import com.clozex.shop.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(User user, CreateOrderDto request);

    Page<OrderDto> getOrders(User user, Pageable pageable);

    List<OrderItemDto> getItems(User user, Long orderId);

    OrderItemDto getItem(User user, Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto request);

    void deleteOrder(Long orderId);

    OrderDto getOrderById(Long orderId);
}
