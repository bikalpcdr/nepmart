package com.bikalpa.nepmart.core.cart.service;

import com.bikalpa.nepmart.core.cart.dto.request.AddCartItemRequestDto;
import com.bikalpa.nepmart.core.cart.dto.request.UpdateCartItemRequestDto;
import com.bikalpa.nepmart.core.cart.dto.response.CartResponseDto;
import com.bikalpa.nepmart.core.cart.entity.Cart;

import java.util.UUID;

public interface CartService {
    CartResponseDto getMyCart();
    CartResponseDto addItem(AddCartItemRequestDto request);
    CartResponseDto updateItem(UUID itemUuid, UpdateCartItemRequestDto request);
    CartResponseDto removeItem(UUID itemUuid);
    Cart getOrCreateActiveCart();
    void clearCart(Cart cart);
}
