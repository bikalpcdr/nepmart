package com.bikalpa.nepmart.core.cart.repo;

import com.bikalpa.nepmart.core.cart.entity.CartItem;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepo extends BaseRepository<CartItem> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);

    List<CartItem> findByCartId(Integer cartId);
}
