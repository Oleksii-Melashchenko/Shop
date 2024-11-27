package com.clozex.shop.controller;

import com.clozex.shop.dto.BookDto;
import com.clozex.shop.dto.BookSearchParametersDto;
import com.clozex.shop.dto.CreateBookRequestDto;
import com.clozex.shop.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints to managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Getting all books from db", description = """
            Can be sorted by parameters:
            - title
            - author
            """)
    public Page<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting book by id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creating new book in db", description = """
            Creates a new book in the database.
            Example request body:
            {
                "title": "Title",
                "author": "Author",
                "isbn": "1", (Unique)
                "price": 1, (Can`t be lower then 0)
                "description": "description", (Optional)
                "coverImage": "https://example.com/cover.jpg" (Optional)
            }
            """)
    public BookDto createBook(@Valid @RequestBody CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleting book by id")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updating book in db by id", description = """
            Updates book in the database by id.
            Example request body:
            {
                "title": "Title",
                "author": "Author",
                "isbn": "1", (Unique)
                "price": 1, (Can`t be lower then 0)
                "description": "description", (Optional)
                "coverImage": "https://example.com/cover.jpg" (Optional)
            }
            """)
    public BookDto updateBookById(@PathVariable Long id,
                                  @RequestBody CreateBookRequestDto requestDto) {
        return bookService.updateById(id, requestDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Searching books with parameters", description = """
            Searching and soring books using the following parameters:
            - title
            - author
            """)
    public Page<BookDto> search(@Valid BookSearchParametersDto searchParams,
                                @PageableDefault Pageable pageable) {
        return bookService.searchBooks(searchParams, pageable);
    }
}
