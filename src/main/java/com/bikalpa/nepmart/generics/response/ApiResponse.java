package com.bikalpa.nepmart.generics.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * @author bikalpa.chaudharii
 * @project nepmart
 * @created 26/10/13
 */
@Data
@Builder
@JsonPropertyOrder({"success", "timestamp", "message", "path", "data"})
public class ApiResponse<T> {
    private Boolean success;
    private LocalDateTime timestamp;
    private String message;
    private String path;
    private T data;

    public static <T> ApiResponse<T> success(T data, String path) {
        return success(data, path, null);
    }

    public static <T> ApiResponse<T> success(T data, String path, String message) {
        return ApiResponse.<T>builder()
                .success(TRUE)
                .timestamp(LocalDateTime.now())
                .path(path)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .success(FALSE)
                .timestamp(LocalDateTime.now())
                .path(path)
                .message(message)
                .build();
    }
}
