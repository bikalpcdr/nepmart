package com.bikalpa.nepmart.generics.utility;

import com.bikalpa.nepmart.exception.GenericUncheckedException;
import com.bikalpa.nepmart.generics.entity.BaseEntity;
import com.bikalpa.nepmart.generics.repo.BaseRepository;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@UtilityClass
public class EntityLookup {

    /**
     * Generic lookup that reports {@code RESOURCE_NOT_FOUND}. Prefer the
     * {@link #findByUuid(BaseRepository, UUID, String, String)} overload when the
     * caller has a module-specific error code.
     */
    public <T extends BaseEntity> T findByUuid(BaseRepository<T> repository, UUID uuid, String resourceName) {
        return repository.findByUuid(uuid).orElseThrow(() -> new GenericUncheckedException(
                resourceName + " not found with uuid: " + uuid, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND));
    }

    /**
     * Lookup that lets the caller supply the exact message and error code, so a
     * 404 reflects the entity being fetched (e.g. {@code USER_NOT_FOUND}) rather
     * than a generic resource error.
     */
    public <T extends BaseEntity> T findByUuid(BaseRepository<T> repository, UUID uuid,
                                                String message, String errorCode) {
        return repository.findByUuid(uuid).orElseThrow(() -> new GenericUncheckedException(
                message, errorCode, HttpStatus.NOT_FOUND));
    }
}
