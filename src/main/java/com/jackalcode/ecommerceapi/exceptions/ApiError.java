package com.jackalcode.ecommerceapi.exceptions;

import java.time.Instant;

public record ApiError(
        ErrorCode code,
        String message,
        Instant timestamp,
        String path
) {
    public ApiError(ErrorCode code, String message, String path) {

        this(code, message, Instant.now(), path);
    }
}
