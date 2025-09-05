package com.fusionsoft.cnd.com.auth.domain.dto;

import org.slf4j.MDC;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        String requestId
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", null, data, MDC.get("requestId"));
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null, MDC.get("requestId"));
    }
}

