package com.bikalpa.nepmart.seeder;

import com.bikalpa.nepmart.core.permission.entity.Permission;
import com.bikalpa.nepmart.core.permission.entity.RolePermission;
import com.bikalpa.nepmart.core.permission.repo.PermissionRepo;
import com.bikalpa.nepmart.core.permission.repo.RolePermissionRepo;
import com.bikalpa.nepmart.core.role.entity.Role;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.enums.RoleScope;
import com.bikalpa.nepmart.core.role.repo.RoleRepo;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.user.enums.UserType;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds a single Super Admin account with full permissions on first startup.
 *
 * <p>Runs as a {@link CommandLineRunner} so the whole context (repositories,
 * password encoder, JPA auditing) is ready. Every step is idempotent - each
 * create is guarded by an {@code exists} check - so restarting the app never
 * produces duplicates.
 *
 * <p>Disabled by setting {@code nepmart.seeder.admin.enabled=false} (recommended
 * once real admin accounts exist). The password lives here only for local
 * development; move it to an env var / secret before any shared deployment.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "nepmart.seeder.admin.enabled", matchIfMissing = true)
public class AdminDataSeeder implements CommandLineRunner {

    /* Default credentials for local development - override via properties. */
    @Value("${nepmart.seeder.admin.email:nepmart@yopmail.com}")
    private String adminEmail;

    @Value("${nepmart.seeder.admin.password:Test@1234}")
    private String adminPassword;

    private static final String ADMIN_ROLE_CODE = "SUPER_ADMIN";
    private static final String ADMIN_ROLE_NAME = "Super Admin";

    /* module -> CRUD actions granted to the seeded Super Admin role. */
    private static final List<String> MODULES =
            List.of("user", "role", "vendor", "permission", "user_role");
    private static final List<String> ACTIONS =
            List.of("create", "read", "update", "delete");

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final UserRoleRepo userRoleRepo;
    private final PermissionRepo permissionRepo;
    private final RolePermissionRepo rolePermissionRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        Role adminRole = seedSuperAdminRole();
        seedPermissions(adminRole);
        User admin = seedAdminUser();
        assignRole(admin, adminRole);

        log.info("Admin seeding complete - {} / role {}", adminEmail, ADMIN_ROLE_CODE);
    }

    /**
     * A system-scoped role with no vendor; unique on {@code code} alone.
     */
    private Role seedSuperAdminRole() {
        return roleRepo.findByCode(ADMIN_ROLE_CODE).orElseGet(() -> {
            Role role = new Role();
            role.setName(ADMIN_ROLE_NAME);
            role.setCode(ADMIN_ROLE_CODE);
            role.setScope(RoleScope.SYSTEM);
            role.setVendor(null);
            role.setIsSystemRole(Boolean.TRUE);
            return roleRepo.save(role);
        });
    }

    /**
     * Grants the role full CRUD on every module, creating any missing permissions
     * and linking them to the role.
     */
    private void seedPermissions(Role role) {
        for (String module : MODULES) {
            for (String action : ACTIONS) {
                String code = module + ":" + action;

                Permission permission = permissionRepo.findByCode(code).orElseGet(() -> {
                    Permission created = new Permission();
                    created.setCode(code);
                    created.setName(action + " " + module);
                    created.setModule(module);
                    created.setAction(action);
                    return permissionRepo.save(created);
                });

                if (!rolePermissionRepo.existsByRoleIdAndPermissionId(role.getId(), permission.getId())) {
                    RolePermission link = new RolePermission();
                    link.setRole(role);
                    link.setPermission(permission);
                    rolePermissionRepo.save(link);
                }
            }
        }
    }

    private User seedAdminUser() {
        return userRepo.findByEmailIgnoreCase(adminEmail).orElseGet(() -> {
            User user = new User();
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setStatus(UserStatus.ACTIVE);
            user.setUserType(UserType.ADMIN);
            user.setIsActive(Boolean.TRUE);
            return userRepo.save(user);
        });
    }

    /**
     * Binds the admin user to the Super Admin role. A system role is vendor-less,
     * so the uniqueness check is {@code (user, role, vendor is null)}.
     */
    private void assignRole(User user, Role role) {
        if (!userRoleRepo.existsByUserRoleVendor(user.getId(), role.getId(), null)) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setVendor(null);
            userRoleRepo.save(userRole);
        }
    }
}
