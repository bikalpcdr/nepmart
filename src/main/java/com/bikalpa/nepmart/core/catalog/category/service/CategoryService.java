package com.bikalpa.nepmart.core.catalog.category.service;

import com.bikalpa.nepmart.core.catalog.category.dto.request.CategoryRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.request.CategorySearchRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.response.CategoryResponseDto;
import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
public interface CategoryService {
    void createCategory(CategoryRequestDto request);

    CategoryResponseDto getCategory(UUID uuid);

    CategoryResponseDto getCategoryBySlug(String slug);

    BasePaginationResponseDto<CategoryResponseDto> getCategories(CategorySearchRequestDto request);

    List<CategoryResponseDto> getActiveCategories();

    void updateCategory(UUID uuid, CategoryRequestDto request);

    void deleteCategory(UUID uuid);

    Category getEntity(UUID uuid);
}
