package com.clozex.shop.dto.cart;

import jakarta.validation.constraints.Positive;

public record CartItemUpdateDto(
        @Positive
        int quantity
) {
}
