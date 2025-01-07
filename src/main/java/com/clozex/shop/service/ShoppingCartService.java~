package com.clozex.shop.service;

import com.clozex.shop.dto.cart.CartItemUpdateDto;
import com.clozex.shop.dto.cart.CreateItemDto;
import com.clozex.shop.dto.cart.ShoppingCartDto;
import com.clozex.shop.model.User;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(User user, CreateItemDto request);

    ShoppingCartDto getShoppingCart(User user);

    ShoppingCartDto updateQuantity(User user, Long itemId, CartItemUpdateDto request);

    void deleteItem(Long id);

    void clearCart(User user);

}
