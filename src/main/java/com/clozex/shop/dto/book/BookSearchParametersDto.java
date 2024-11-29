package com.clozex.shop.dto.book;

public record BookSearchParametersDto(
        String[] title,
        String[] author
) {
}
