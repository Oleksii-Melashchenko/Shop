package com.clozex.shop.controller.book;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.repository.book.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final Long TEST_BOOK_ID = 1L;
    private static final String TEST_TITLE = "title1";
    private static final String TEST_AUTHOR = "Author1";
    private static final String TEST_ISBN = "111";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(11);
    private static final Set<Long> TEST_CATEGORIES = Set.of(1L);
    private static final String ALTERNATIVE_TEST_TITLE = "title2";
    private static final String ALTERNATIVE_TEST_AUTHOR = "Author2";
    private static final String ALTERNATIVE_TEST_ISBN = "222";
    private static final BigDecimal ALTERNATIVE_TEST_PRICE = BigDecimal.valueOf(22);
    private static final Set<Long> ALTERNATIVE_TEST_CATEGORIES = Set.of(2L);
    private static final int EXPECTED_LENGTH = 2;
    private static final String PAGE_PARAM_NAME = "page";
    private static final String PAGE_PARAM_VALUE = "0";
    private static final String SIZE_PARAM_NAME = "size";
    private static final String SIZE_PARAM_VALUE = "2";

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
    @DisplayName("Check createBook endpoint by valid request")
    void createBook_validRequestDto_ReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(TEST_TITLE);
        requestDto.setAuthor(TEST_AUTHOR);
        requestDto.setIsbn(TEST_ISBN);
        requestDto.setPrice(TEST_PRICE);
        requestDto.setCategoryIds(TEST_CATEGORIES);

        BookDto expect = createDefaultBookDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expect, actual, "id");
    }

    @WithMockUser()
    @Test
    @DisplayName("Check functionality of getAllBooks method")
    void getAllBooks_DbWithData_ReturnPageOfBookDtoWithoutCategoryIds()
            throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books")
                        .param(PAGE_PARAM_NAME, PAGE_PARAM_VALUE)
                        .param(SIZE_PARAM_NAME, SIZE_PARAM_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDtoWithoutCategoryIds> actualContent = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {
                }
        );

        Assertions.assertNotNull(actualContent);
        Assertions.assertFalse(actualContent.isEmpty());
        Assertions.assertEquals(EXPECTED_LENGTH, actualContent.size());
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Check functionality of deleteBook method")
    void deleteBook_ValidData_ReturnNoContentStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        assertFalse(bookRepository.existsById(TEST_BOOK_ID));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Check functionality of updateBook method")
    void update_ValidData_ReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = createDefaultRequestDto();

        BookDto expect = new BookDto();
        expect.setTitle(ALTERNATIVE_TEST_TITLE);
        expect.setAuthor(ALTERNATIVE_TEST_AUTHOR);
        expect.setIsbn(ALTERNATIVE_TEST_ISBN);
        expect.setPrice(ALTERNATIVE_TEST_PRICE);
        expect.setCategoryIds(ALTERNATIVE_TEST_CATEGORIES);
        expect.setId(TEST_BOOK_ID);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/books/{id}", TEST_BOOK_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expect, actual);
    }

    @WithMockUser()
    @Test
    @DisplayName("Check functionality of findBookById method")
    void findBookById_ValidId_ReturnBookDto() throws Exception {
        BookDto expect = createDefaultBookDto();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", TEST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expect, actual);
    }

    private BookDto createDefaultBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(TEST_TITLE);
        bookDto.setAuthor(TEST_AUTHOR);
        bookDto.setIsbn(TEST_ISBN);
        bookDto.setPrice(TEST_PRICE);
        bookDto.setCategoryIds(TEST_CATEGORIES);
        bookDto.setId(TEST_BOOK_ID);
        return bookDto;
    }

    private CreateBookRequestDto createDefaultRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(TEST_TITLE);
        requestDto.setAuthor(TEST_AUTHOR);
        requestDto.setIsbn(TEST_ISBN);
        requestDto.setPrice(TEST_PRICE);
        requestDto.setCategoryIds(TEST_CATEGORIES);
        return requestDto;
    }
}
