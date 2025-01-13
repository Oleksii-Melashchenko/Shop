package com.clozex.shop.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.mapper.BookMapper;
import com.clozex.shop.model.Book;
import com.clozex.shop.model.Category;
import com.clozex.shop.repository.book.BookRepository;
import com.clozex.shop.service.impl.BookServiceImpl;
import com.clozex.shop.util.BookTestUtil;
import com.clozex.shop.util.CategoryTestUtil;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private static final Long FIRST_BOOK_ID = 1L;
    private static final String FIRST_BOOK_NAME = "Book_1";
    private static final String FIRST_BOOK_AUTHOR = "Author_1";
    private static final String FIRST_BOOK_ISBN = "111";
    private static final BigDecimal FIRST_BOOK_PRICE = BigDecimal.valueOf(20.00);
    private static final Long SECOND_BOOK_ID = 2L;
    private static final String SECOND_BOOK_NAME = "Book_2";
    private static final String SECOND_BOOK_AUTHOR = "Author_1";
    private static final String SECOND_BOOK_ISBN = "222";
    private static final String UPDATED_BOOK_NAME = "Update_Book_1.1";
    private static final String UPDATED_BOOK_AUTHOR = "Update_Author_1.1";
    private static final BigDecimal UPDATED_BOOK_PRICE = BigDecimal.valueOf(35.00);
    private static final Long INCORRECT_BOOK_ID = 111L;
    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Category_1";
    private static final String CATEGORY_DESCRIPTION = "Description_1";
    private static Category category;
    private static Book book2;
    private static Book book1;
    private static Book updatedBook;
    private static BookDto expectedDto;
    private static BookDto updatedExpectedDto;
    private static CreateBookRequestDto requestDto;
    private static CreateBookRequestDto updatedRequestDto;
    private static List<Book> books;

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @BeforeAll
    static void beforeAll() {
        book1 = BookTestUtil.createBook(FIRST_BOOK_ID,
                FIRST_BOOK_NAME,
                FIRST_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                FIRST_BOOK_PRICE);

        category = CategoryTestUtil.createCategory(CATEGORY_ID, CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        book2 = BookTestUtil.createBookWithCategory(SECOND_BOOK_ID,
                SECOND_BOOK_NAME,
                SECOND_BOOK_AUTHOR,
                SECOND_BOOK_ISBN,
                Set.of(category));

        requestDto = BookTestUtil.createBookRequestDto(FIRST_BOOK_NAME,
                FIRST_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                FIRST_BOOK_PRICE);

        expectedDto = BookTestUtil.createExpectedBookDto(FIRST_BOOK_ID,
                FIRST_BOOK_NAME,
                FIRST_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                FIRST_BOOK_PRICE);

        books = List.of(book1, book2);

        updatedBook = BookTestUtil.createBook(FIRST_BOOK_ID,
                UPDATED_BOOK_NAME,
                UPDATED_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                UPDATED_BOOK_PRICE);

        updatedRequestDto = BookTestUtil.createBookRequestDto(UPDATED_BOOK_NAME,
                UPDATED_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                FIRST_BOOK_PRICE);

        updatedExpectedDto = BookTestUtil.createExpectedBookDto(FIRST_BOOK_ID,
                UPDATED_BOOK_NAME,
                UPDATED_BOOK_AUTHOR,
                FIRST_BOOK_ISBN,
                UPDATED_BOOK_PRICE);
    }

    @Test
    @DisplayName("Saving valid book")
    void saveBook_WhenValidBookPassed_BookIsSaved() {
        //given
        when(bookRepository.save(book1)).thenReturn(book1);

        when(bookMapper.toDto(book1)).thenReturn(expectedDto);

        when(bookMapper.toModel(requestDto)).thenReturn(book1);

        //when
        BookDto actual = bookService.save(requestDto);

        //then
        assertNotNull(actual, "Saved book is null");
        assertEquals(expectedDto, actual, "Saved book is not equal to expected");

        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book1);
        verify(bookMapper, times(1)).toDto(book1);

        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Find all books with valid pageable")
    void findAllBooks_WithValidPageable_ShouldReturnPageOfBookDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        List<BookDto> expectedDtoList = books.stream()
                .map(book -> new BookDto()
                        .setId(book.getId())
                        .setTitle(book.getTitle())
                        .setAuthor(book.getAuthor())
                        .setPrice(book.getPrice())
                        .setIsbn(book.getIsbn()))
                .toList();

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        when(bookMapper.toDto(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return new BookDto()
                    .setId(book.getId())
                    .setTitle(book.getTitle())
                    .setAuthor(book.getAuthor())
                    .setPrice(book.getPrice())
                    .setIsbn(book.getIsbn());
        });

        Page<BookDto> expectedDtoPage = new PageImpl<>(expectedDtoList, pageable, books.size());

        //when
        Page<BookDto> actual = bookService.findAll(pageable);

        //then
        assertNotNull(actual, "Returned page should not be null");
        assertEquals(expectedDtoPage.getContent(), actual.getContent(),
                "Found books are not equal to expected");
        assertEquals(expectedDtoPage.getTotalElements(), actual.getTotalElements(),
                "Total elements don`t match");
        assertEquals(expectedDtoPage.getPageable(), actual.getPageable(), "Pageable doesn`t match");

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(books.size())).toDto(any(Book.class));

        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by id")
    void findBookById_ValidId_BookIsFound() {
        //given
        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book1));

        when(bookMapper.toDto(book1)).thenReturn(expectedDto);

        //when
        BookDto actual = bookService.getById(FIRST_BOOK_ID);

        //then
        assertNotNull(actual, "Found book is null");
        assertEquals(expectedDto, actual, "Found book is not equal to expected");

        verify(bookRepository, times(1)).findById(FIRST_BOOK_ID);
        verify(bookMapper, times(1)).toDto(book1);

        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by id with non-existent id")
    void findBookById_NonExistentId_ThrowsException() {
        //given
        when(bookRepository.findById(INCORRECT_BOOK_ID)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.getById(INCORRECT_BOOK_ID)
        );
    }

    @Test
    @DisplayName("Delete book by id")
    void deleteBookById_BookIsDeleted() {
        //when
        bookService.deleteById(FIRST_BOOK_ID);

        //then
        verify(bookRepository, times(1)).deleteById(FIRST_BOOK_ID);

        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book by id")
    void updateBookById_BookIsUpdatedSuccessfully() {
        // given
        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book1));

        doNothing().when(bookMapper).updateBookFromDto(updatedRequestDto, book1);

        when(bookRepository.save(book1)).thenReturn(updatedBook);

        when(bookMapper.toDto(updatedBook)).thenReturn(updatedExpectedDto);

        // when
        BookDto actual = bookService.updateById(FIRST_BOOK_ID, updatedRequestDto);

        // then
        assertNotNull(actual, "Updated book is null");
        assertEquals(updatedExpectedDto, actual, "Updated book is not equal to expected");

        verify(bookRepository, times(1)).findById(FIRST_BOOK_ID);
        verify(bookMapper, times(1)).updateBookFromDto(updatedRequestDto, book1);
        verify(bookRepository, times(1)).save(book1);
        verify(bookMapper, times(1)).toDto(updatedBook);

        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book by id with non-existent id")
    void updateBookById_NonExistentId_ThrowsException() {
        //given
        when(bookRepository.findById(INCORRECT_BOOK_ID)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(INCORRECT_BOOK_ID, updatedRequestDto));
    }

    @Test
    @DisplayName("Find all books by category id")
    void findAllBooksByCategoryId_ExistingCategory_ReturnsBooks() {
        //given
        Long categoryId = 1L;

        Pageable pageable = Pageable.unpaged();

        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(bookPage);

        when(bookMapper.toWithoutCategoryIdDto(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return new BookDtoWithoutCategoryIds(book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getPrice(),
                    book.getDescription(),
                    book.getCoverImage());
        });

        List<BookDtoWithoutCategoryIds> expectedDtoList = books.stream()
                .map(book -> new BookDtoWithoutCategoryIds(book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPrice(),
                        book.getDescription(),
                        book.getCoverImage()))
                .toList();

        //when
        Page<BookDtoWithoutCategoryIds> actual = bookService.getBooksByCategoryId(categoryId,
                pageable);

        //then
        assertNotNull(actual, "Found books is null");
        assertFalse(actual.getContent().isEmpty(), "Found books list is empty");
        assertEquals(expectedDtoList.size(), actual.getContent().size(),
                "Number of books doesn't match");

        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);
        verify(bookMapper, times(books.size())).toWithoutCategoryIdDto(any(Book.class));

        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books by category id with non-existent id")
    void findAllBooksByCategoryId_NonExistentCategory_ReturnsEmptyPage() {
        // given
        Long categoryId = 100L;

        Pageable pageable = Pageable.unpaged();

        Page<Book> bookPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(bookPage);

        // when
        Page<BookDtoWithoutCategoryIds> actual = bookService.getBooksByCategoryId(categoryId,
                pageable);

        // then
        assertNotNull(actual, "Found books is null");
        assertTrue(actual.getContent().isEmpty(), "Found books list is not empty");
        assertEquals(0, actual.getContent().size(), "Number of books doesn't match");

        verify(bookRepository, times(1)).findAllByCategoryId(categoryId, pageable);

        verifyNoMoreInteractions(bookRepository);
    }

}
