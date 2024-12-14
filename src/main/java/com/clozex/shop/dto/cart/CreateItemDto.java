package com.clozex.shop.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateItemDto(
        @NotNull
        @Positive
        Long bookId,
        @Positive
        int quantity
) {
}
