package com.bikalpa.nepmart.core.order.repo;

import com.bikalpa.nepmart.core.order.entity.CustomerOrder;
import com.bikalpa.nepmart.core.order.enums.OrderStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends BaseRepository<CustomerOrder> {
    Page<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    Optional<CustomerOrder> findByUuidAndUserId(java.util.UUID uuid, Integer userId);

    long countByUserId(Integer userId);

    @Query("select distinct o from CustomerOrder o join o.items i where i.vendor.id = :vendorId order by o.createdAt desc")
    Page<CustomerOrder> findByVendorId(@Param("vendorId") Integer vendorId, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);
}
