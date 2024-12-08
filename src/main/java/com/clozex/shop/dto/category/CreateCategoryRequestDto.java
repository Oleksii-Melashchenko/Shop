package com.clozex.shop.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDto(
        @NotBlank(message = "Name can`t be empty")
        String name,
        String description
) {
}
