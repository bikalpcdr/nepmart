package com.bikalpa.nepmart.generics.pagination;

import lombok.*;

import java.util.List;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePaginationResponseDto<T> {
    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private Boolean isFirst;

    private Boolean isLast;

    private Boolean isEmpty;
}
