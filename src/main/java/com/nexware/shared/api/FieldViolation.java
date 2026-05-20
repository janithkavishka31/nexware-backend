package com.nexware.shared.api;

public record FieldViolation(
        String field,
        String message,
        Object rejectedValue
) {
}
