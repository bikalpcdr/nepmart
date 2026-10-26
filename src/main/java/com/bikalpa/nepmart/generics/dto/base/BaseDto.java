package com.bikalpa.nepmart.generics.dto.base;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Getter
@Setter
public class BaseDto {
    private UUID uuid;
    private Boolean isActive = Boolean.TRUE;
}
