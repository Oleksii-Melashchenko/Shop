package com.clozex.shop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.clozex.shop.dto.book.BookDto;
import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.book.CreateBookRequestDto;
import com.clozex.shop.repository.book.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
        CreateBookRequestDto requestDto = createDefaultRequestDto();

        BookDto expect = createDefaultBookDto();

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
        reflectionEquals(expect, actual, "id");
    }

    @WithMockUser()
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

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        List<BookDtoWithoutCategoryIds> actualContent = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {
                }
        );

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
        CreateBookRequestDto requestDto = createDefaultRequestDto();

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
        reflectionEquals(expect, actual);
    }

    @WithMockUser()
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
        reflectionEquals(expect, actual);
    }

    private BookDto createDefaultBookDto() {
        return new BookDto()
                .setTitle(TEST_TITLE)
                .setAuthor(TEST_AUTHOR)
                .setIsbn(TEST_ISBN)
                .setPrice(TEST_PRICE)
                .setCategoryIds(TEST_CATEGORIES)
                .setId(TEST_BOOK_ID);
    }

    private CreateBookRequestDto createDefaultRequestDto() {
        return new CreateBookRequestDto()
                .setTitle(TEST_TITLE)
                .setAuthor(TEST_AUTHOR)
                .setIsbn(TEST_ISBN)
                .setPrice(TEST_PRICE)
                .setCategoryIds(TEST_CATEGORIES);
    }
}
