package com.clozex.shop.mapper;

import com.clozex.shop.config.MapperConfig;
import com.clozex.shop.dto.category.CategoryDto;
import com.clozex.shop.dto.category.CreateCategoryRequestDto;
import com.clozex.shop.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryRequestDto categoryDto);

    void updateCategoryFromDto(CreateCategoryRequestDto categoryDto,
                               @MappingTarget Category category);
}
