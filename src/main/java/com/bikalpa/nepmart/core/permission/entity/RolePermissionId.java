package com.bikalpa.nepmart.core.permission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "permission_id")
    private Integer permissionId;
}
