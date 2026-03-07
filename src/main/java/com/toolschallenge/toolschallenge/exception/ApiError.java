package com.toolschallenge.toolschallenge.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> errors
) {

    public ApiError(Instant timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null);
    }

}
