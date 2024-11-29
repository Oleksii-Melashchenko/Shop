package com.clozex.shop.service.impl;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookSearchParametersDto;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.mapper.BookMapper;
import com.clozex.shop.model.Book;
import com.clozex.shop.repository.book.BookRepository;
import com.clozex.shop.repository.book.BookSpecificationBuilder;
import com.clozex.shop.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
            .map(bookMapper::toDto);
    }

    @Override
    public BookDto getById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t get book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        bookMapper.updateBookFromDto(requestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> searchBooks(BookSearchParametersDto searchParams, Pageable pageable) {
        Specification<Book> specification = bookSpecificationBuilder.build(searchParams);
        return bookRepository.findAll(specification,pageable)
                .map(bookMapper::toDto);
    }
}
