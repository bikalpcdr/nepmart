package com.bikalpa.nepmart.core.user.entity;

import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.user.enums.UserType;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Enumerated(EnumType.STRING)
    /* columnDefinition default lets Hibernate's ddl-auto=update add this NOT NULL
     * column to an already-populated users table (existing rows become CUSTOMER). */
    @Column(name = "user_type", nullable = false, length = 20,
            columnDefinition = "varchar(20) not null default 'CUSTOMER'")
    private UserType userType = UserType.CUSTOMER;
}
