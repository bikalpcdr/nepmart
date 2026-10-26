package com.bikalpa.nepmart.core.user.repo;

import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Repository
public interface UserRepo extends BaseRepository<User> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
            select u from User u
            where (:email = '' or lower(u.email) like lower(concat('%', :email, '%') ) )
              and (:status is null or u.status = :status)
              and (:active is null or u.isActive = :active)
            """)
    Page<User> searchUsers(
            @Param("email") String email,
            @Param("status") UserStatus status,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
