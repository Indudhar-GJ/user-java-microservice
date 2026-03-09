package com.CropLink.User.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
    private final T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    /** For responses that have no data payload (e.g. logout, send-OTP) */
    public static ApiResponse<Void> of(String message) {
        return new ApiResponse<>(message, null);
    }
}
