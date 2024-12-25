package com.clozex.shop.repository.cart;

import com.clozex.shop.model.ShoppingCart;
import com.clozex.shop.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long id);

    @Query("SELECT sc FROM ShoppingCart sc JOIN FETCH sc.cartItems WHERE sc.user = :user")
    Optional<ShoppingCart> findByUserWithCartItems(@Param("user") User user);
}
