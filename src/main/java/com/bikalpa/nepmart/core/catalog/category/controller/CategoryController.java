package com.bikalpa.nepmart.core.catalog.category.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.catalog.category.dto.request.CategoryRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.request.CategorySearchRequestDto;
import com.bikalpa.nepmart.core.catalog.category.dto.response.CategoryResponseDto;
import com.bikalpa.nepmart.core.catalog.category.service.CategoryService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = CATEGORY)
    public BasePaginationResponseDto<CategoryResponseDto> getAll(@Valid @ModelAttribute CategorySearchRequestDto request) {
        return categoryService.getCategories(request);
    }

    @GetMapping("/active")
    @SuccessMessage(action = FETCHED, entity = CATEGORY)
    public List<CategoryResponseDto> getActive() {
        return categoryService.getActiveCategories();
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = CATEGORY)
    public CategoryResponseDto getById(@PathVariable UUID uuid) {
        return categoryService.getCategory(uuid);
    }

    @GetMapping("/slug/{slug}")
    @SuccessMessage(action = FETCHED, entity = CATEGORY)
    public CategoryResponseDto getBySlug(@PathVariable String slug) {
        return categoryService.getCategoryBySlug(slug);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('category:create')")
    @SuccessMessage(action = CREATED, entity = CATEGORY)
    public void create(@Valid @RequestBody CategoryRequestDto request) {
        categoryService.createCategory(request);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('category:update')")
    @SuccessMessage(action = UPDATED, entity = CATEGORY)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody CategoryRequestDto request) {
        categoryService.updateCategory(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('category:delete')")
    @SuccessMessage(action = DELETED, entity = CATEGORY)
    public void delete(@PathVariable UUID uuid) {
        categoryService.deleteCategory(uuid);
    }
}
