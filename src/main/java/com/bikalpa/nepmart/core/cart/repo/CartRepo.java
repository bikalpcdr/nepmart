package com.bikalpa.nepmart.core.cart.repo;

import com.bikalpa.nepmart.core.cart.entity.Cart;
import com.bikalpa.nepmart.core.cart.enums.CartStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends BaseRepository<Cart> {
    Optional<Cart> findByUserIdAndStatus(Integer userId, CartStatus status);
}
