package com.bikalpa.nepmart.core.permission.entity;

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
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_permissions_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String code;

    @Column(nullable = false, length = 100)
    private String module;

    @Column(nullable = false, length = 50)
    private String action;
}
