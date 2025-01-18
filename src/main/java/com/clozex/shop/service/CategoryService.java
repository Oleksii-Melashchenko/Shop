package com.clozex.shop.service;

import com.clozex.shop.dto.category.CategoryDto;
import com.clozex.shop.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto save(CreateCategoryRequestDto requestDto);

    Page<CategoryDto> findAll(Pageable pageable);

    void deleteById(Long id);

    CategoryDto getById(Long id);

    CategoryDto updateById(Long id, CreateCategoryRequestDto requestDto);
}
