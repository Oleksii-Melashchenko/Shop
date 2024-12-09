package com.clozex.shop.service;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.book.BookSearchParametersDto;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    Page<BookDto> searchBooks(BookSearchParametersDto searchParams, Pageable pageable);

    Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id,
                                                                Pageable pageable);
}
