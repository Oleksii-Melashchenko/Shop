package com.clozex.shop.controller;

import com.clozex.shop.dto.cart.CartItemUpdateDto;
import com.clozex.shop.dto.cart.CreateItemDto;
import com.clozex.shop.dto.cart.ShoppingCartDto;
import com.clozex.shop.model.User;
import com.clozex.shop.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart management", description = "Endpoints to managing shopping carts")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @Operation(summary = "Adding book to cart")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addBook(@RequestBody @Valid CreateItemDto request,
                                   Authentication authentication) {
        return shoppingCartService.addCartItem((User) authentication.getPrincipal(), request);
    }

    @GetMapping
    @Operation(summary = "Getting shopping cart for user")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart((User) authentication.getPrincipal());
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Updating book quantity")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto updateQuantity(@PathVariable Long cartItemId,
                                          @RequestBody @Valid CartItemUpdateDto request,
                                          Authentication authentication) {
        return shoppingCartService.updateQuantity((User) authentication.getPrincipal(),
                cartItemId, request);
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Deleting item from cart")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long cartItemId) {
        shoppingCartService.deleteItem(cartItemId);
    }
}
