package com.bikalpa.nepmart.generics.pagination;

import com.bikalpa.nepmart.generics.dto.base.BaseDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
public class BasePaginationDto extends BaseDto {
    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(100)
    private Integer size = 10;

    private String sortBy = "id";

    @Pattern(regexp = "(?i)ASC|DESC")
    private String sortDirection = "DESC";
}
