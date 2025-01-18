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
        category = new Category().setId(CATEGORY_ID)
                .setName(CATEGORY_NAME)
                .setDescription(CATEGORY_DESCRIPTION);

        requestDto = new CreateCategoryRequestDto(CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        expectedDto = new CategoryDto(CATEGORY_ID,
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION);

        categories = List.of(category,category2 = new Category()
                .setId(2L)
                .setName("Category_2")
                .setDescription("Description_2")
        );

        updatedCategory = new Category().setId(CATEGORY_ID)
                .setName("Update_Category_1.1")
                .setDescription("Update_Description_1.1");

        updatedRequestDto = new CreateCategoryRequestDto("Update_Category_1.1",
                "Update_Description_1.1");

        updatedExpectedDto = new CategoryDto(CATEGORY_ID, "Update_Category_1.1",
                "Update_Description_1.1");
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

        verify(categoryMapper, times(1)).toModel(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);

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

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(categories.size())).toDto(any(Category.class));

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Delete category by id")
    void deleteCategoryById_CategoryIsDeleted() {
        //when
        categoryService.deleteById(CATEGORY_ID);

        //then
        verify(categoryRepository, times(1)).deleteById(CATEGORY_ID);

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

        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryMapper, times(1)).toDto(category);

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by id with non-existent id")
    void findCategoryById_NonExistentId_ThrowsException() {
        //given
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(INCORRECT_CATEGORY_ID)
        );
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

        verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        verify(categoryMapper, times(1)).updateCategoryFromDto(updatedRequestDto, category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(updatedCategory);

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Update category by id with non-existent id")
    void updateCategoryById_NonExistentId_ThrowsException() {
        //given
        when(categoryRepository.findById(INCORRECT_CATEGORY_ID)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(INCORRECT_CATEGORY_ID, updatedRequestDto));
    }
}
