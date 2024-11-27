package com.clozex.shop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        @Size(max = 255)
        String author,

        @NotBlank
        @Size(max = 255)
        String isbn,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal price,

        String description,

        @Size(max = 255)
        String coverImage
) {
}

