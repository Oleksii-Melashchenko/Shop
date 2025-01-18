package com.clozex.shop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.repository.book.BookRepository;
import com.clozex.shop.util.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final Long TEST_BOOK_ID = 1L;
    private static final String TEST_TITLE = "book1";
    private static final String TEST_AUTHOR = "author1";
    private static final String TEST_ISBN = "isbn1";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(10.99);
    private static final String TEST_DESCRIPTION = "description1";
    private static final String TEST_COVER_IMAGE = "cover_image1";
    private static final Set<Long> TEST_CATEGORIES = Set.of(2L);
    private static final String ALTERNATIVE_TEST_TITLE = "title2";
    private static final String ALTERNATIVE_TEST_AUTHOR = "Author2";
    private static final String ALTERNATIVE_TEST_ISBN = "222";
    private static final Long INCORRECT_BOOK_ID = 111L;
    private static final BigDecimal ALTERNATIVE_TEST_PRICE = BigDecimal.valueOf(22);
    private static final Set<Long> ALTERNATIVE_TEST_CATEGORIES = Set.of(2L);
    private static final int EXPECTED_LENGTH = 5;

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void setup(@Autowired WebApplicationContext webApplicationContext,
                      @Autowired DataSource dataSource) {
        cleanUpDb(dataSource);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/add-test-books.sql"));
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        cleanUpDb(dataSource);
    }

    @SneakyThrows
    static void cleanUpDb(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("db/clean-test-books.sql")
            );
        }
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Creating book with valid requestDto")
    void createBook_validRequestDto_ReturnBookDto() throws Exception {
        //given
        CreateBookRequestDto requestDto = createDefaultRequestDto().setIsbn("111");

        BookDto expect = createDefaultBookDto().setIsbn("111");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //when
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        //then
        assertNotNull(actual);
        assertTrue(reflectionEquals(expect, actual, "id"));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Creating book with invalid requestDto")
    void createBook_PriceLessThanZeroInRequestDto_ReturnBadRequest() throws Exception {
        //given
        CreateBookRequestDto requestDto = createDefaultRequestDto().setIsbn("111")
                .setPrice(BigDecimal.valueOf(-20));

        String expectedErrorMessage = "Field: price Minimal value is 0.0";

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //when
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        //then
        assertNotNull(responseContent);
        assertTrue(responseContent.contains(expectedErrorMessage),
                "Response should contain error message: " + expectedErrorMessage);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting all books")
    void getAllBooks_ReturnPageOfBookDtoWithoutCategoryIds()
            throws Exception {
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        PageResponse<BookDtoWithoutCategoryIds> actualPage = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        List<BookDtoWithoutCategoryIds> actualContent = actualPage.getContent();

        //then
        assertNotNull(actualContent);
        assertFalse(actualContent.isEmpty());
        assertEquals(EXPECTED_LENGTH, actualContent.size());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Deleting book with valid id")
    void deleteBook_ValidData_ReturnNoContentStatus() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertFalse(bookRepository.existsById(TEST_BOOK_ID));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Updating book with valid requestDto")
    void update_ValidData_ReturnBookDto() throws Exception {
        //given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setAuthor(ALTERNATIVE_TEST_AUTHOR)
                .setTitle(ALTERNATIVE_TEST_TITLE)
                .setTitle(ALTERNATIVE_TEST_TITLE)
                .setIsbn(ALTERNATIVE_TEST_ISBN)
                .setPrice(ALTERNATIVE_TEST_PRICE)
                .setCategoryIds(ALTERNATIVE_TEST_CATEGORIES);

        BookDto expect = new BookDto()
                .setTitle(ALTERNATIVE_TEST_TITLE)
                .setAuthor(ALTERNATIVE_TEST_AUTHOR)
                .setIsbn(ALTERNATIVE_TEST_ISBN)
                .setPrice(ALTERNATIVE_TEST_PRICE)
                .setCategoryIds(ALTERNATIVE_TEST_CATEGORIES)
                .setId(TEST_BOOK_ID);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/books/{id}", TEST_BOOK_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        //then
        assertNotNull(actual);
        assertTrue(reflectionEquals(expect, actual));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting book by id with valid id")
    void findBookById_ValidId_ReturnBookDto() throws Exception {
        //given
        BookDto expect = createDefaultBookDto();

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        //then
        assertNotNull(actual);
        assertTrue(reflectionEquals(expect, actual));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting book by id with invalid id")
    void findBookById_InvalidId_ReturnNotFound() throws Exception {
        //given
        String expectedErrorMessage = "Can`t get book by id: " + INCORRECT_BOOK_ID;

        // When
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}",
                                INCORRECT_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        // Then
        assertNotNull(responseContent);
        assertTrue(responseContent.contains(expectedErrorMessage),
                "Response should contain error message: " + expectedErrorMessage);
    }

    private BookDto createDefaultBookDto() {
        return new BookDto()
                .setTitle(TEST_TITLE)
                .setAuthor(TEST_AUTHOR)
                .setIsbn(TEST_ISBN)
                .setPrice(TEST_PRICE)
                .setCategoryIds(TEST_CATEGORIES)
                .setId(TEST_BOOK_ID)
                .setDescription(TEST_DESCRIPTION)
                .setCoverImage(TEST_COVER_IMAGE);
    }

    private CreateBookRequestDto createDefaultRequestDto() {
        return new CreateBookRequestDto()
                .setTitle(TEST_TITLE)
                .setAuthor(TEST_AUTHOR)
                .setIsbn(TEST_ISBN)
                .setPrice(TEST_PRICE)
                .setCategoryIds(TEST_CATEGORIES)
                .setDescription(TEST_DESCRIPTION)
                .setCoverImage(TEST_COVER_IMAGE);
    }
}
