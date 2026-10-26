package com.bikalpa.nepmart.generics.pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationMapper {
    public static <T> BasePaginationResponseDto<T> toResponse(Page<T> page) {
        return BasePaginationResponseDto.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .isEmpty(page.isEmpty())
                .build();
    }
}
