package com.bikalpa.nepmart.generics.response;

import com.bikalpa.nepmart.annotation.SuccessMessage;
import com.bikalpa.nepmart.constant.MessageConstant;
import jakarta.annotation.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@Nullable MethodParameter returnType, @Nullable Class converterType) {
        /*
         * Exclude Springdoc/OpenAPI endpoints from wrapping.
         * Without this, the raw OpenAPI JSON spec gets wrapped inside ApiResponse,
         * which breaks the Swagger UI entirely since it expects the raw spec format.
         */
        if (returnType == null) return false;
        String declaringClass = returnType.getDeclaringClass().getName();
        return !declaringClass.startsWith("org.springdoc") &&
                !declaringClass.startsWith("org.springframework.boot.actuate");
    }

    @Override
    public @Nullable Object beforeBodyWrite(
            @Nullable Object body,
            @Nullable MethodParameter returnType,
            @Nullable MediaType selectedContentType,
            @Nullable Class selectedConverterType,
            @Nullable ServerHttpRequest request,
            @Nullable ServerHttpResponse response) {

        if (body instanceof ApiResponse) {
            return body;
        }

        String message = null;
        SuccessMessage successMessage = returnType != null ? returnType.getMethodAnnotation(SuccessMessage.class) : null;
        if (successMessage != null) {
            message = String.format(MessageConstant.SUCCESS_TEMPLATE, successMessage.entity(), successMessage.action());
        }
        return ApiResponse.success(body, request != null ? request.getURI().getPath() : null, message);
    }
}
