package com.clozex.shop.controller;

import com.clozex.shop.dto.order.CreateOrderDto;
import com.clozex.shop.dto.order.OrderDto;
import com.clozex.shop.dto.order.OrderItemDto;
import com.clozex.shop.dto.order.UpdateOrderStatusDto;
import com.clozex.shop.model.User;
import com.clozex.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints to managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creating new order in db")
    @PreAuthorize("hasRole('USER')")
    public OrderDto createOrder(@Valid @RequestBody CreateOrderDto request,
                                Authentication authentication) {
        return orderService.createOrder((User) authentication.getPrincipal(), request);
    }

    @GetMapping
    @Operation(summary = "Getting orders for user from db")
    @PreAuthorize("hasRole('USER')")
    public Page<OrderDto> getOrders(Authentication authentication,
                                    Pageable pageable) {
        return orderService.getOrders((User) authentication.getPrincipal(), pageable);
    }

    @GetMapping("/admin/{orderId}/items")
    @Operation(summary = "Getting items for order from db as admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderItemDto> getItemsForAdmin(@PathVariable Long orderId) {
        return orderService.getItemsForAdmin(orderId);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Getting items for order from db as user")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemDto> getItemsForUser(@PathVariable Long orderId,
                                              Authentication authentication) {
        return orderService.getItemsForUser((User) authentication.getPrincipal(), orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Getting item for order from db")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto getItem(@PathVariable Long orderId,
                            @PathVariable Long itemId,
                            Authentication authentication) {
        return orderService.getItem((User) authentication.getPrincipal(), orderId, itemId);
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Updating order status in db")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
                                      @Valid @RequestBody UpdateOrderStatusDto request) {
        return orderService.updateOrderStatus(orderId, request);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleting order from db")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Getting order from db by id")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto getOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }
}
