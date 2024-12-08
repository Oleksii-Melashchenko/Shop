package com.clozex.shop.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookRequestDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Author cannot be empty")
    @Size(max = 255)
    private String author;

    @NotBlank(message = "Isbn cannot be empty")
    @Size(max = 255)
    private String isbn;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Minimal value is 0.0")
    private BigDecimal price;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String coverImage;

    @NotEmpty(message = "Please, add categories for book")
    private Set<Long> categoryIds;
}

