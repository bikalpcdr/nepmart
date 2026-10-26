package com.bikalpa.nepmart.core.auth.service;

import com.bikalpa.nepmart.constant.MessageConstant;
import com.bikalpa.nepmart.core.user.entity.User;
import com.bikalpa.nepmart.core.user.enums.UserStatus;
import com.bikalpa.nepmart.core.permission.repo.RolePermissionRepo;
import com.bikalpa.nepmart.core.role.entity.UserRole;
import com.bikalpa.nepmart.core.role.repo.UserRoleRepo;
import com.bikalpa.nepmart.core.user.repo.UserRepo;
import com.bikalpa.nepmart.exception.GenericUncheckedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Bridges the {@code users} table into Spring Security.
 *
 * <p>Every account gets a single {@code ROLE_USER} authority for now - the
 * role/permission tables exist but are not wired into authentication yet, so
 * fine-grained authorization would need {@link #getAuthorities(User)} extended
 * to read them.
 *
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final RolePermissionRepo rolePermissionRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(
                () -> new GenericUncheckedException(
                        MessageConstant.USER_NOT_FOUND_MESSAGE + email,
                        MessageConstant.USER_NOT_FOUND,
                        NOT_FOUND));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new GenericUncheckedException(
                    MessageConstant.ACCOUNT_NOT_ACTIVE_MESSAGE,
                    MessageConstant.ACCOUNT_NOT_ACTIVE,
                    FORBIDDEN);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(Boolean.FALSE.equals(user.getIsActive()))
                .accountLocked(user.getStatus() == UserStatus.INACTIVE)
                .authorities(getAuthorities(user))
                .build();
    }

    /**
     * Baseline role plus every distinct active permission code reachable
     * through the user's active role assignments.
     */
    private List<SimpleGrantedAuthority> getAuthorities(User user) {
        List<UserRole> assignments = userRoleRepo.findActiveByUserIdWithRoleAndVendors(user.getId());
        if (assignments.isEmpty()) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        List<Integer> roleIds = assignments.stream().map(ur -> ur.getRole().getId()).toList();
        java.util.List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        rolePermissionRepo.findActiveByRoleIdsWithPermission(roleIds).stream()
                .map(rp -> rp.getPermission().getCode())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        return List.copyOf(authorities);
    }
}
