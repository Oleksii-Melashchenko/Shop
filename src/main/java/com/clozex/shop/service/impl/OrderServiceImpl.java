package com.clozex.shop.service.impl;

import com.clozex.shop.dto.order.CreateOrderDto;
import com.clozex.shop.dto.order.OrderDto;
import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.dto.order.UpdateOrderStatusDto;
import com.clozex.shop.exception.EmptyCartException;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.mapper.OrderItemMapper;
import com.clozex.shop.mapper.OrderMapper;
import com.clozex.shop.model.CartItem;
import com.clozex.shop.model.Order;
import com.clozex.shop.model.OrderItem;
import com.clozex.shop.model.ShoppingCart;
import com.clozex.shop.model.Status;
import com.clozex.shop.model.User;
import com.clozex.shop.repository.cart.ShoppingCartRepository;
import com.clozex.shop.repository.order.OrderItemRepository;
import com.clozex.shop.repository.order.OrderRepository;
import com.clozex.shop.service.OrderService;
import com.clozex.shop.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderDto createOrder(User user, CreateOrderDto request) {
        Order order = initOrder(user, request.shippingAddress());
        ShoppingCart shoppingCart = getShoppingCartForUser(user);
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new EmptyCartException(
                    "Cannot place an order. Should be at least one item in the shopping cart");
        }
        order.setTotal(calculateTotalAndAddItems(order, shoppingCart));
        shoppingCartService.clearCart(user);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderDto> getOrders(User user, Pageable pageable) {
        Page<Order> orders = isAdmin(user)
                ? orderRepository.findAll(pageable)
                : orderRepository.findAllByUserId(user.getId(), pageable);
        return orders.map(orderMapper::toDto);
    }

    @Override
    public List<OrderItemDto> getItemsForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId)
                );
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getItemsForUser(User user, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: "
                        + orderId + " for user: "
                        + user.getId()));

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderMapper.toDto(orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found")));
    }

    @Override
    public OrderItemDto getItem(User user, Long orderId, Long itemId) {
        return orderItemRepository.findByOrderIdAndId(orderId, itemId);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found by id: "
                        + orderId));
        order.setStatus(request.status());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    private Order initOrder(User user, String shippingAddress) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Status.PENDING);
        return order;
    }

    public ShoppingCart getShoppingCartForUser(User user) {
        return shoppingCartRepository.findByUserWithCartItems(user)
                .orElseThrow(
                        () -> new EntityNotFoundException("Shopping cart not found for user id: "
                        + user.getId())
                );
    }

    private BigDecimal calculateTotalAndAddItems(Order order, ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> createOrderItem(order, cartItem))
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        order.getOrderItems().add(orderItem);
        return orderItem;
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
