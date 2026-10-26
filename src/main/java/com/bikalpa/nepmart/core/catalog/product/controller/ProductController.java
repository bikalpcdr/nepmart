package com.bikalpa.nepmart.core.catalog.product.controller;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.request.ProductSearchRequestDto;
import com.bikalpa.nepmart.core.catalog.product.dto.response.ProductResponseDto;
import com.bikalpa.nepmart.core.catalog.product.service.ProductService;
import com.bikalpa.nepmart.generics.pagination.BasePaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.bikalpa.nepmart.constant.MessageConstant.*;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @SuccessMessage(action = FETCHED, entity = PRODUCT)
    public BasePaginationResponseDto<ProductResponseDto> search(@Valid @ModelAttribute ProductSearchRequestDto request) {
        return productService.searchProducts(request);
    }

    @GetMapping("/featured")
    @SuccessMessage(action = FETCHED, entity = PRODUCT)
    public List<ProductResponseDto> featured(@RequestParam(defaultValue = "12") int limit) {
        return productService.getFeaturedProducts(limit);
    }

    @GetMapping("/{uuid}")
    @SuccessMessage(action = FETCHED, entity = PRODUCT)
    public ProductResponseDto getById(@PathVariable UUID uuid) {
        return productService.getProduct(uuid);
    }

    @GetMapping("/slug/{slug}")
    @SuccessMessage(action = FETCHED, entity = PRODUCT)
    public ProductResponseDto getBySlug(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:create')")
    @SuccessMessage(action = CREATED, entity = PRODUCT)
    public void create(@Valid @RequestBody ProductRequestDto request) {
        productService.createProduct(request);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('product:update')")
    @SuccessMessage(action = UPDATED, entity = PRODUCT)
    public void update(@PathVariable UUID uuid, @Valid @RequestBody ProductRequestDto request) {
        productService.updateProduct(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('product:delete')")
    @SuccessMessage(action = DELETED, entity = PRODUCT)
    public void delete(@PathVariable UUID uuid) {
        productService.deleteProduct(uuid);
    }

    @PatchMapping("/{uuid}/publish")
    @PreAuthorize("hasAuthority('product:update')")
    @SuccessMessage(action = UPDATED, entity = PRODUCT)
    public void publish(@PathVariable UUID uuid) {
        productService.publishProduct(uuid);
    }

    @PatchMapping("/{uuid}/archive")
    @PreAuthorize("hasAuthority('product:update')")
    @SuccessMessage(action = UPDATED, entity = PRODUCT)
    public void archive(@PathVariable UUID uuid) {
        productService.archiveProduct(uuid);
    }
}
