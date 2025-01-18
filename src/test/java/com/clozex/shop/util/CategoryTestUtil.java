package com.clozex.shop.util;

import com.clozex.shop.dto.category.CategoryDto;
import com.clozex.shop.dto.category.CreateCategoryRequestDto;
import com.clozex.shop.model.Category;

public class CategoryTestUtil {
    public static Category createCategory(long id, String name, String description) {
        return new Category().setId(id)
                .setName(name)
                .setDescription(description);
    }

    public static CreateCategoryRequestDto createCategoryRequestDto(String name,
                                                                    String description) {
        return new CreateCategoryRequestDto(name, description);
    }

    public static CategoryDto createCategoryDto(long id, String name, String description) {
        return new CategoryDto(id, name, description);
    }
}
