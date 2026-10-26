package com.bikalpa.nepmart.core.notification.repo;

import com.bikalpa.nepmart.core.notification.entity.PushSubscription;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepo extends BaseRepository<PushSubscription> {
    List<PushSubscription> findByUserIdAndIsActiveTrue(Integer userId);

    Optional<PushSubscription> findByUserIdAndEndpoint(Integer userId, String endpoint);
}
