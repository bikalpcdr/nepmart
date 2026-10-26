package com.bikalpa.nepmart.core.cart.service.impl;

import com.bikalpa.nepmart.common.security.VendorAccess;
import com.bikalpa.nepmart.core.cart.dto.request.AddCartItemRequestDto;
import com.bikalpa.nepmart.core.cart.dto.request.UpdateCartItemRequestDto;
import com.bikalpa.nepmart.core.cart.dto.response.CartItemResponseDto;
import com.bikalpa.nepmart.core.cart.dto.response.CartResponseDto;
import com.bikalpa.nepmart.core.cart.entity.Cart;
import com.bikalpa.nepmart.core.cart.entity.CartItem;
import com.bikalpa.nepmart.core.cart.enums.CartStatus;
import com.bikalpa.nepmart.core.cart.repo.CartItemRepo;
import com.bikalpa.nepmart.core.cart.repo.CartRepo;
import com.bikalpa.nepmart.core.cart.service.CartService;
import com.bikalpa.nepmart.core.catalog.product.entity.Product;
import com.bikalpa.nepmart.core.catalog.product.enums.ProductStatus;
import com.bikalpa.nepmart.core.catalog.product.service.ProductService;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.utility.EntityLookup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductService productService;
    private final VendorAccess vendorAccess;

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getMyCart() {
        return toResponse(getOrCreateActiveCart());
    }

    @Override
    public CartResponseDto addItem(AddCartItemRequestDto request) {
        int qty = request.getQuantity() == null ? 1 : request.getQuantity();
        if (qty < 1) {
            throw new GenericUncheckedException("Quantity must be at least 1", "CART_QTY_INVALID", BAD_REQUEST);
        }
        Cart cart = getOrCreateActiveCart();
        Product product = productService.getEntity(request.getProductUuid());
        requireSaleable(product);
        CartItem item = cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        int newQty = qty + (item == null ? 0 : item.getQuantity());
        if (newQty > product.getStockQuantity()) {
            throw new GenericUncheckedException("Only " + product.getStockQuantity() + " in stock",
                    "CART_STOCK_EXCEEDED", CONFLICT);
        }
        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setIsActive(Boolean.TRUE);
            cart.getItems().add(item);
        } else {
            item.setQuantity(newQty);
        }
        cartItemRepo.save(item);
        log.info("Cart item added: product {} qty {}", product.getSlug(), qty);
        return toResponse(cart);
    }

    @Override
    public CartResponseDto updateItem(UUID itemUuid, UpdateCartItemRequestDto request) {
        Cart cart = getOrCreateActiveCart();
        CartItem item = EntityLookup.findByUuid(cartItemRepo, itemUuid,
                "Cart item not found with uuid: " + itemUuid, "CART_ITEM_NOT_FOUND");
        requireOwnership(cart, item);
        if (item.getQuantity() + 0 < 1 || request.getQuantity() < 1) {
            throw new GenericUncheckedException("Quantity must be at least 1", "CART_QTY_INVALID", BAD_REQUEST);
        }
        if (request.getQuantity() > item.getProduct().getStockQuantity()) {
            throw new GenericUncheckedException("Only " + item.getProduct().getStockQuantity() + " in stock",
                    "CART_STOCK_EXCEEDED", CONFLICT);
        }
        item.setQuantity(request.getQuantity());
        cartItemRepo.save(item);
        return toResponse(cart);
    }

    @Override
    public CartResponseDto removeItem(UUID itemUuid) {
        Cart cart = getOrCreateActiveCart();
        CartItem item = EntityLookup.findByUuid(cartItemRepo, itemUuid,
                "Cart item not found with uuid: " + itemUuid, "CART_ITEM_NOT_FOUND");
        requireOwnership(cart, item);
        cart.getItems().remove(item);
        cartItemRepo.delete(item);
        return toResponse(cart);
    }

    @Override
    public Cart getOrCreateActiveCart() {
        User user = vendorAccess.currentUser();
        return cartRepo.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setIsActive(Boolean.TRUE);
                    return cartRepo.save(cart);
                });
    }

    @Override
    public void clearCart(Cart cart) {
        cartItemRepo.deleteAll(cartItemRepo.findByCartId(cart.getId()));
        cart.getItems().clear();
    }

    private void requireOwnership(Cart cart, CartItem item) {
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new GenericUncheckedException("Cart item does not belong to your cart",
                    "CART_ITEM_FORBIDDEN", FORBIDDEN);
        }
    }

    private void requireSaleable(Product product) {
        if (!Boolean.TRUE.equals(product.getIsActive()) || product.getStatus() != ProductStatus.PUBLISHED) {
            throw new GenericUncheckedException("Product is not available for purchase",
                    "PRODUCT_NOT_SALEABLE", CONFLICT);
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 1) {
            throw new GenericUncheckedException("Product is out of stock", "PRODUCT_OUT_OF_STOCK", CONFLICT);
        }
    }

    private CartResponseDto toResponse(Cart cart) {
        var items = cartItemRepo.findByCartId(cart.getId()).stream().map(item -> {
            BigDecimal unit = item.getProduct().getPrice();
            CartItemResponseDto dto = CartItemResponseDto.builder()
                    .productUuid(item.getProduct().getUuid())
                    .productName(item.getProduct().getName())
                    .productSlug(item.getProduct().getSlug())
                    .imageUrl(item.getProduct().getImageUrl())
                    .vendorName(item.getProduct().getVendor().getName())
                    .unitPrice(unit)
                    .quantity(item.getQuantity())
                    .lineTotal(unit.multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            dto.setUuid(item.getUuid());
            dto.setIsActive(item.getIsActive());
            return dto;
        }).toList();
        BigDecimal subtotal = items.stream()
                .map(item -> item.getLineTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalItems = items.stream().mapToInt(item -> item.getQuantity()).sum();
        CartResponseDto response = CartResponseDto.builder()
                .status(cart.getStatus())
                .items(items)
                .subtotal(subtotal)
                .totalItems(totalItems)
                .build();
        response.setUuid(cart.getUuid());
        response.setIsActive(cart.getIsActive());
        return response;
    }
}
