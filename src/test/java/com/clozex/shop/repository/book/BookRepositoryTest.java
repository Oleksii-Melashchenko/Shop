package com.clozex.shop.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.clozex.shop.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db/add-test-books.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/clean-test-books.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by existing category id")
    void findAllBooksByCategoryId_ExistingCategory_ReturnsTBooks() {
        // given
        Long categoryId = 1L;

        // when
        Page<Book> books = bookRepository.findAllByCategoryId(categoryId, Pageable.unpaged());

        // then
        assertEquals(3, books.getContent().size());
        assertEquals("book2", books.getContent().getFirst().getTitle());
    }

    @Test
    @DisplayName("Find all books by non-existent category id")
    void findAllBooksByCategoryId_NonExistentCategory_ReturnsEmptyPage() {
        // given
        Long categoryId = 100L;

        // when
        Page<Book> books = bookRepository.findAllByCategoryId(categoryId, Pageable.unpaged());

        // then
        assertEquals(0, books.getContent().size());
    }

    @Test
    @DisplayName("Find all books by null category id")
    void findAllBooksByCategoryId_NullCategory_ReturnsEmptyPage() {
        // given
        Long categoryId = null;

        // when
        Page<Book> books = bookRepository.findAllByCategoryId(categoryId, Pageable.unpaged());

        // then
        assertEquals(0, books.getContent().size());
    }

    @Test
    @DisplayName("Find all books by empty category id")
    void findAllBooksByCategoryId_EmptyCategory_ReturnsEmptyPage() {
        // given
        Long categoryId = 0L;

        // when
        Page<Book> books = bookRepository.findAllByCategoryId(categoryId, Pageable.unpaged());

        // then
        assertEquals(0, books.getContent().size());
    }

}
