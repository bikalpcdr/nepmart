package com.bikalpa.nepmart.core.catalog.category.service.impl;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.catalog.category.dto.request.CategoryRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.request.CategorySearchRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.response.CategoryResponseDto;
import com.bikalpa.nepmart.core.catalog.category.entity.Category;
import com.bikalpa.nepmart.core.catalog.category.enums.CategoryStatus;
import com.bikalpa.nepmart.core.catalog.category.repo.CategoryRepo;
import com.bikalpa.nepmart.core.catalog.category.service.CategoryService;
import com.bikalpa.nepmart.core.catalog.category.utility.CategoryConverter;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import com.bikalpa.nepmart.generics.pagination.PaginationMapper;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.PageConstant.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "id", "name", "slug", "displayOrder", "createdAt", "updatedAt");

    private final CategoryRepo categoryRepo;

    @Override
    public void createCategory(CategoryRequestDto request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new GenericUncheckedException("Category name is required", "CATEGORY_NAME_REQUIRED", BAD_REQUEST);
        }
        String slug = resolveSlug(request.getSlug(), request.getName());
        if (categoryRepo.existsBySlug(slug)) {
            throw new GenericUncheckedException("Category slug already exists", "CATEGORY_SLUG_ALREADY_EXISTS", CONFLICT);
        }
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setStatus(CategoryStatus.ACTIVE);
        category.setIsActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);
        if (request.getParentUuid() != null) {
            category.setParent(findByUuidOrThrow(request.getParentUuid()));
        }
        categoryRepo.save(category);
        log.info("Category created with slug {}", slug);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategory(UUID uuid) {
        return CategoryConverter.toResponseDto(findByUuidOrThrow(uuid));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryBySlug(String slug) {
        Category category = categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new GenericUncheckedException(
                        "Category not found with slug: " + slug, "CATEGORY_NOT_FOUND", NOT_FOUND));
        return CategoryConverter.toResponseDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationResponseDto<CategoryResponseDto> getCategories(CategorySearchRequestDto request) {
        Page<CategoryResponseDto> result = categoryRepo.searchCategories(
                normalize(request.getName()),
                request.getStatus(),
                Boolean.TRUE.equals(request.getRootOnly()),
                buildPageable(request.getPage(), request.getSize(), request.getSortBy(), request.getSortDirection())
        ).map(CategoryConverter::toResponseDto);
        return PaginationMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getActiveCategories() {
        return categoryRepo.findByIsActiveTrueAndStatusOrderByDisplayOrderAsc(CategoryStatus.ACTIVE)
                .stream().map(CategoryConverter::toResponseDto).toList();
    }

    @Override
    public void updateCategory(UUID uuid, CategoryRequestDto request) {
        Category category = findByUuidOrThrow(uuid);
        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName().trim());
        }
        String slug = resolveSlug(request.getSlug(), category.getName());
        if (categoryRepo.existsBySlugAndIdNot(slug, category.getId())) {
            throw new GenericUncheckedException("Category slug already exists", "CATEGORY_SLUG_ALREADY_EXISTS", CONFLICT);
        }
        category.setSlug(slug);
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) category.setIsActive(request.getActive());
        if (request.getParentUuid() != null) {
            Category parent = findByUuidOrThrow(request.getParentUuid());
            if (parent.getId().equals(category.getId())) {
                throw new GenericUncheckedException("Category cannot be its own parent", "CATEGORY_INVALID_PARENT", BAD_REQUEST);
            }
            category.setParent(parent);
        }
        categoryRepo.save(category);
        log.info("Category updated with uuid {}", uuid);
    }

    @Override
    public void deleteCategory(UUID uuid) {
        Category category = findByUuidOrThrow(uuid);
        category.setIsActive(Boolean.FALSE);
        categoryRepo.save(category);
        log.info("Category soft-deleted with uuid {}", uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getEntity(UUID uuid) {
        return findByUuidOrThrow(uuid);
    }

    private Category findByUuidOrThrow(UUID uuid) {
        return EntityLookup.findByUuid(categoryRepo, uuid, "Category not found with uuid: " + uuid, "CATEGORY_NOT_FOUND");
    }

    private String resolveSlug(String slug, String name) {
        String resolved = toSlug((slug == null || slug.isBlank()) ? name : slug);
        if (resolved.isBlank()) {
            throw new GenericUncheckedException("Category slug is required", "CATEGORY_SLUG_REQUIRED", BAD_REQUEST);
        }
        return resolved;
    }

    private String toSlug(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    private Pageable buildPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNo = page == null ? DEFAULT_PAGE : page;
        int pageSize = size == null ? DEFAULT_SIZE : size;
        if (pageNo < DEFAULT_PAGE) {
            throw new GenericUncheckedException(MessageConstant.INVALID_PAGE_MESSAGE, MessageConstant.INVALID_PAGE, BAD_REQUEST);
        }
        if (pageSize < MIN_SIZE || pageSize > MAX_SIZE) {
            throw new GenericUncheckedException(MessageConstant.INVALID_PAGE_SIZE_MESSAGE, MessageConstant.INVALID_PAGE_SIZE, BAD_REQUEST);
        }
        String sort = sortBy == null ? "displayOrder" : sortBy;
        if (!SORT_FIELDS.contains(sort)) {
            throw new GenericUncheckedException(MessageConstant.INVALID_SORT_FIELD_MESSAGE + sort, MessageConstant.INVALID_SORT_FIELD, BAD_REQUEST);
        }
        String direction = sortDirection == null ? "ASC" : sortDirection.toUpperCase(Locale.ROOT);
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new GenericUncheckedException(MessageConstant.INVALID_SORT_DIRECTION_MESSAGE, MessageConstant.INVALID_SORT_DIRECTION, BAD_REQUEST);
        }
        return PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sort));
    }
}
