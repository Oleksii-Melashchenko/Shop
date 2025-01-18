package com.clozex.shop.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.clozex.shop.dto.category.CategoryDto;
import com.clozex.shop.dto.category.CreateCategoryRequestDto;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.mapper.CategoryMapper;
import com.clozex.shop.model.Category;
import com.clozex.shop.repository.category.CategoryRepository;
import com.clozex.shop.service.impl.CategoryServiceImpl;
import com.clozex.shop.util.CategoryTestUtil;
import java.util.List;
import java.util.Optional;
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
class CategoryServiceImplTest {
    private static final Long CATEGORY_ID = 1L;
    private static final Long INCORRECT_CATEGORY_ID = 111L;
    private static final String CATEGORY_NAME = "Category_1";
    private static final String CATEGORY_DESCRIPTION = "Description_1";
    private static final String UPDATED_CATEGORY_NAME = "Update_Category_1.1";
    private static final String UPDATED_CATEGORY_DESCRIPTION = "Update_Description_1.1";
    private static final int WANTED_TIMES_OF_INVOKE = 1;
    private static Category category;
    private static Category category2;
    private static Category updatedCategory;
    private static CategoryDto expectedDto;
    private static CategoryDto updatedExpectedDto;
    private static CreateCategoryRequestDto requestDto;
    private static CreateCategoryRequestDto updatedRequestDto;
    private static List<Category> categories;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @BeforeAll
    static void beforeAll() {
        category = CategoryTestUtil.createCategory(CATEGORY_ID,
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        requestDto = CategoryTestUtil.createCategoryRequestDto(CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        expectedDto = CategoryTestUtil.createCategoryDto(CATEGORY_ID,
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        categories = List.of(category,category2 = CategoryTestUtil.createCategory(2L,
                "Category_2",
                "Description_2"));

        updatedCategory = CategoryTestUtil.createCategory(CATEGORY_ID,
                UPDATED_CATEGORY_NAME,
                UPDATED_CATEGORY_DESCRIPTION);

        updatedRequestDto = CategoryTestUtil.createCategoryRequestDto(UPDATED_CATEGORY_NAME,
                UPDATED_CATEGORY_DESCRIPTION);

        updatedExpectedDto = CategoryTestUtil.createCategoryDto(CATEGORY_ID,
        UPDATED_CATEGORY_NAME,
        UPDATED_CATEGORY_DESCRIPTION);
    }

    @Test
    @DisplayName("Saving valid category")
    void saveBook_WhenValidCategoryPassed_CategoryIsSaved() {
        //given
        when(categoryRepository.save(category)).thenReturn(category);

        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);

        //when
        CategoryDto actual = categoryService.save(requestDto);

        //then
        assertNotNull(actual, "Saved category is null");
        assertEquals(expectedDto, actual, "Saved category is not equal to expected");

        verify(categoryMapper, times(WANTED_TIMES_OF_INVOKE)).toModel(requestDto);
        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).save(category);
        verify(categoryMapper, times(WANTED_TIMES_OF_INVOKE)).toDto(category);

        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Find all categories with valid pageable")
    void findAllBooks_WithValidPageable_ShouldReturnPageOfCategoryDto() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        List<CategoryDto> expectedDtoList = categories.stream()
                .map(category -> new CategoryDto(category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .toList();

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        when(categoryMapper.toDto(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            return new CategoryDto(category.getId(),
                    category.getName(),
                    category.getDescription()
            );
        });

        Page<CategoryDto> expectedDtoPage = new PageImpl<>(expectedDtoList, pageable,
                categories.size());

        //when
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        //then
        assertNotNull(actual, "Returned page should not be null");
        assertEquals(expectedDtoPage.getContent(), actual.getContent(),
                "Found categories are not equal to expected");
        assertEquals(expectedDtoPage.getTotalElements(), actual.getTotalElements(),
                "Total elements don`t match");
        assertEquals(expectedDtoPage.getPageable(), actual.getPageable(), "Pageable doesn`t match");

        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).findAll(pageable);
        verify(categoryMapper, times(categories.size())).toDto(any(Category.class));

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete category by id")
    void deleteCategoryById_CategoryIsDeleted() {
        //when
        categoryService.deleteById(CATEGORY_ID);

        //then
        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).deleteById(CATEGORY_ID);

        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Find category by id")
    void findCategoryById_ValidId_CategoryIsFound() {
        //given
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        //when
        CategoryDto actual = categoryService.getById(CATEGORY_ID);

        //then
        assertNotNull(actual, "Found book is null");
        assertEquals(expectedDto, actual, "Found book is not equal to expected");

        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).findById(CATEGORY_ID);
        verify(categoryMapper, times(WANTED_TIMES_OF_INVOKE)).toDto(category);

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by id with non-existent id")
    void findCategoryById_NonExistentId_ThrowsException() {
        //given
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());
        String expectedMessage = "Can`t get category by id: " + INCORRECT_CATEGORY_ID;

        // when
        EntityNotFoundException thrownException = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(INCORRECT_CATEGORY_ID)
        );

        // then
        assertEquals(expectedMessage, thrownException.getMessage(),
                "Error message should match the expected message.");
    }

    @Test
    @DisplayName("Update category by id")
    void updateCategoryById_CategoryIsUpdatedSuccessfully() {
        // given
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        doNothing().when(categoryMapper).updateCategoryFromDto(updatedRequestDto, category);

        when(categoryRepository.save(category)).thenReturn(updatedCategory);

        when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedExpectedDto);

        // when
        CategoryDto actual = categoryService.updateById(CATEGORY_ID, updatedRequestDto);

        // then
        assertNotNull(actual, "Updated category is null");
        assertEquals(updatedExpectedDto, actual, "Updated category is not equal to expected");

        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).findById(CATEGORY_ID);
        verify(categoryMapper, times(WANTED_TIMES_OF_INVOKE))
                .updateCategoryFromDto(updatedRequestDto, category);
        verify(categoryRepository, times(WANTED_TIMES_OF_INVOKE)).save(category);
        verify(categoryMapper, times(WANTED_TIMES_OF_INVOKE)).toDto(updatedCategory);

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category by id with non-existent id")
    void updateCategoryById_NonExistentId_ThrowsException() {
        //given
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());
        String expectedMessage = "Category with id " + INCORRECT_CATEGORY_ID + " not found";

        // when
        EntityNotFoundException thrownException = assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(INCORRECT_CATEGORY_ID, updatedRequestDto)
        );

        // then
        assertEquals(expectedMessage, thrownException.getMessage(),
                "Error message should match the expected message.");
    }
}
