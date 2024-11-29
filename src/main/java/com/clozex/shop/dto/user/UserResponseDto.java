package com.clozex.shop.dto.user;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress
) {
}
