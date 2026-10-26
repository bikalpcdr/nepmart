package com.bikalpa.nepmart.core.catalog.category.dto.request;

import com.bikalpa.nepmart.core.catalog.category.enums.CategoryStatus;
import com.bikalpa.nepmart.generics.pagination.BasePaginationDto;
import lombok.*;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategorySearchRequestDto extends BasePaginationDto {
    private String name;
    private CategoryStatus status;
    private Boolean rootOnly;
}
