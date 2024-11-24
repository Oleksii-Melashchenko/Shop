package com.clozex.shop.dto;

public record BookSearchParametersDto(
        String[] title,
        String[] author) {
}
