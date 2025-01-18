package com.clozex.shop.util;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.model.Book;
import com.clozex.shop.model.Category;
import java.math.BigDecimal;
import java.util.Set;

public class BookTestUtil {
    public static Book createBook(Long id, String name, String author, String isbn,
                                  BigDecimal price) {
        return new Book().setId(id)
                .setTitle(name)
                .setAuthor(author)
                .setPrice(price)
                .setIsbn(isbn);
    }

    public static Book createBookWithCategory(Long id, String name, String author,
                                              String isbn,
                                              Set<Category> categories) {
        return new Book().setId(id)
                .setTitle(name)
                .setAuthor(author)
                .setPrice(BigDecimal.valueOf(20.00))
                .setIsbn(isbn)
                .setCategories(categories);
    }

    public static CreateBookRequestDto createBookRequestDto(String name, String author, String isbn,
                                                            BigDecimal price) {
        return new CreateBookRequestDto().setTitle(name)
                .setAuthor(author)
                .setPrice(price)
                .setIsbn(isbn);
    }

    public static BookDto createExpectedBookDto(Long id, String name, String author, String isbn,
                                                BigDecimal price) {
        return new BookDto().setId(id)
                .setTitle(name)
                .setAuthor(author)
                .setPrice(price)
                .setIsbn(isbn);
    }

}
