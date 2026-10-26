package com.bikalpa.nepmart.core.catalog.category.utility;

import com.bikalpa.nepmart.core.catalog.category.dto.response.CategoryResponseDto;
import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import lombok.experimental.UtilityClass;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@UtilityClass
public class CategoryConverter {

    public static CategoryResponseDto toResponseDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponseDto response = new CategoryResponseDto();
        response.setUuid(category.getUuid());
        response.setIsActive(category.getIsActive());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setStatus(category.getStatus());
        if (category.getParent() != null) {
            response.setParentUuid(category.getParent().getUuid());
            response.setParentName(category.getParent().getName());
        }
        return response;
    }
}
