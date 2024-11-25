package com.clozex.shop.service;

import com.clozex.shop.dto.BookDto;
import com.clozex.shop.dto.BookSearchParametersDto;
import com.clozex.shop.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getById(Long id);

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    Page<BookDto> searchBooks(BookSearchParametersDto searchParams, Pageable pageable);
}
