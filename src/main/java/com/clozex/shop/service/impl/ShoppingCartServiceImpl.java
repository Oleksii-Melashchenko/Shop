package com.clozex.shop.service.impl;

import com.clozex.shop.dto.cart.CartItemUpdateDto;
import com.clozex.shop.dto.cart.CreateItemDto;
import com.clozex.shop.dto.cart.ShoppingCartDto;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.mapper.CartItemMapper;
import com.clozex.shop.mapper.ShoppingCartMapper;
import com.clozex.shop.model.Book;
import com.clozex.shop.model.CartItem;
import com.clozex.shop.model.ShoppingCart;
import com.clozex.shop.model.User;
import com.clozex.shop.repository.book.BookRepository;
import com.clozex.shop.repository.cart.CartItemRepository;
import com.clozex.shop.repository.cart.ShoppingCartRepository;
import com.clozex.shop.repository.user.UserRepository;
import com.clozex.shop.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShoppingCartDto addCartItem(User user, CreateItemDto request) {
        ShoppingCart shoppingCart = getShoppingCartOrCreate(user);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found: "
                        + request.bookId()));
        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(request.bookId()))
                .findFirst()
                .map(existingItem -> updateCartItem(existingItem, request.quantity()))
                .orElseGet(() -> createCartItem(request, shoppingCart, book));
        shoppingCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto getShoppingCart(User user) {
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can`t find cart for user "
                        + user)));
    }

    @Override
    public ShoppingCartDto updateQuantity(User user, Long itemId, CartItemUpdateDto request) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found " + itemId));
        cartItem.setQuantity(request.quantity());
        shoppingCartRepository.save(cartItem.getShoppingCart());
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found " + id));
        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        shoppingCart.getCartItems().remove(cartItem);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void clearCart(User user) {
        ShoppingCart shoppingCart = getShoppingCartOrCreate(user);
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getShoppingCartOrCreate(User user) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseGet(() -> shoppingCartRepository.save(new ShoppingCart(userRepository
                        .findByEmail(user.getEmail())
                        .orElseThrow(
                                () -> new EntityNotFoundException("Can`t find user"
                                        + user)))));
    }

    private CartItem updateCartItem(CartItem cartItem, int additionalQuantity) {
        cartItem.setQuantity(cartItem.getQuantity() + additionalQuantity);
        return cartItem;
    }

    private CartItem createCartItem(CreateItemDto request, ShoppingCart shoppingCart, Book book) {
        CartItem cartItem = cartItemMapper.toModel(request);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        return cartItem;
    }

}
