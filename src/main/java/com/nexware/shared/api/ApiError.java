package com.nexware.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        String code,
        String message,
        String path,
        String traceId,
        List<FieldViolation> fieldViolations
) {

    public static ApiError of(ErrorCode errorCode, String message, String path, String traceId) {
        return new ApiError(errorCode.name(), message, path, traceId, List.of());
    }

    public static ApiError validation(String message, String path, String traceId, List<FieldViolation> violations) {
        return new ApiError(ErrorCode.VALIDATION_FAILED.name(), message, path, traceId, violations);
    }
}
