package com.bikalpa.nepmart.core.catalog.category.dto.response;

import com.bikalpa.nepmart.core.catalog.category.enums.CategoryStatus;
import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import lombok.*;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDto extends BaseDto {
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private UUID parentUuid;
    private String parentName;
    private Integer displayOrder;
    private CategoryStatus status;
}
