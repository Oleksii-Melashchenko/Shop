package com.clozex.shop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderDto(
        @NotBlank(message = "Shipping address can't be blank")
        @Size(min = 5, max = 255, message = "Shipping address must be between 5 and 255 characters")
        String shippingAddress
) {
}
