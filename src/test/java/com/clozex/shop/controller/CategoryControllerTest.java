package com.clozex.shop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import com.clozex.shop.dto.book.BookDtoWithoutCategoryIds;
import com.clozex.shop.dto.category.CategoryDto;
import com.clozex.shop.dto.category.CreateCategoryRequestDto;
import com.clozex.shop.repository.category.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
class CategoryControllerTest {
    private static final Long TEST_CATEGORY_ID = 1L;
    private static final String TEST_CATEGORY_NAME = "category1";
    private static final String TEST_CATEGORY_DESCRIPTION = "description1";
    private static final String ALTERNATIVE_CATEGORY_NAME = "category2";
    private static final String ALTERNATIVE_CATEGORY_DESCRIPTION = "description2";
    private static final String CREATE_CATEGORY_NAME = "createCategoryName";
    private static final String CREATE_CATEGORY_DESCRIPTION = "createCategoryDescription";

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

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

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting all categories")
    void getAllCategories_ReturnPageOfCategoryDto()
            throws Exception {
        //given

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        List<CategoryDto> actualContent = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {
                }
        );

        //then
        assertNotNull(actualContent);
        assertFalse(actualContent.isEmpty());
        assertEquals(3, actualContent.size());
    }

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting all books by category id")
    void getAllBooksByCategoryId_ReturnPageOfBookDtoWithoutCategoryIds()
            throws Exception {
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}/books",
                                TEST_CATEGORY_ID)
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
        assertEquals(3, actualContent.size());
    }

    @WithMockUser(roles = {"USER"})
    @Test
    @DisplayName("Getting category by id with valid id")
    void findCategoryById_ValidId_ReturnCategoryDto() throws Exception {
        //given
        CategoryDto expect = createDefaultCategoryDto();

        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}",
                                TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        //then
        assertNotNull(actual);
        reflectionEquals(expect, actual);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Creating category with valid requestDto")
    void createCategory_validRequestDto_ReturnCategoryDto() throws Exception {
        //given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(CREATE_CATEGORY_NAME,
                CREATE_CATEGORY_DESCRIPTION);

        CategoryDto expect = createDefaultCategoryDto();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //when
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        //then
        assertNotNull(actual);
        reflectionEquals(expect, actual, "id");
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Updating category with valid requestDto")
    void update_ValidData_ReturnCategoryDto() throws Exception {
        //given
        CreateCategoryRequestDto requestDto = createDefaultRequestDto();

        CategoryDto expect = new CategoryDto(TEST_CATEGORY_ID,
                ALTERNATIVE_CATEGORY_NAME,
                ALTERNATIVE_CATEGORY_DESCRIPTION);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}",
                                TEST_CATEGORY_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        //then
        assertNotNull(actual);
        reflectionEquals(expect, actual);
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    @DisplayName("Deleting category with valid id")
    void deleteCategory_ValidData_ReturnNoContentStatus() throws Exception {
        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", TEST_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        assertFalse(categoryRepository.existsById(TEST_CATEGORY_ID));
    }

    private CategoryDto createDefaultCategoryDto() {
        return new CategoryDto(TEST_CATEGORY_ID,
                TEST_CATEGORY_NAME,
                TEST_CATEGORY_DESCRIPTION);
    }

    private CreateCategoryRequestDto createDefaultRequestDto() {
        return new CreateCategoryRequestDto(TEST_CATEGORY_NAME,
                TEST_CATEGORY_DESCRIPTION);
    }
}
