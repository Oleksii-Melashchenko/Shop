package com.clozex.shop.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 255)
        String title,

        @NotBlank(message = "Author cannot be empty")
        @Size(max = 255)
        String author,

        @NotBlank(message = "Isbn cannot be empty")
        @Size(max = 255)
        String isbn,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", message = "Minimal value is 0.0")
        BigDecimal price,

        @Size(max = 255)
        String description,

        @Size(max = 255)
        String coverImage
) {
}

