package com.bikalpa.nepmart.core.vendor.membership.repo;

import com.bikalpa.nepmart.core.vendor.membership.entity.VendorMembership;
import com.bikalpa.nepmart.core.vendor.membership.enums.MembershipStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorMembershipRepo extends BaseRepository<VendorMembership> {
    Optional<VendorMembership> findByUserIdAndVendorId(Integer userId, Integer vendorId);

    List<VendorMembership> findByVendorIdAndStatus(Integer vendorId, MembershipStatus status);

    List<VendorMembership> findByUserId(Integer userId);

    boolean existsByUserIdAndVendorId(Integer userId, Integer vendorId);
}
