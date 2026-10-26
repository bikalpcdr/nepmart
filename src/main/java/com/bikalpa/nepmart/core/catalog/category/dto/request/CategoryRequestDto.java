package com.bikalpa.nepmart.core.catalog.category.dto.request;

import jakarta.validation.constraints.Size;
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
public class CategoryRequestDto {

    @Size(max = 150)
    private String name;

    @Size(max = 180)
    private String slug;

    private String description;

    @Size(max = 500)
    private String imageUrl;

    private UUID parentUuid;

    private Integer displayOrder;

    private Boolean active;
}
