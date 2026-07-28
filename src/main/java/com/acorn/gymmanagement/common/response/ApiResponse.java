package com.acorn.gymmanagement.common.response;

public record ApiResponse<T>(boolean success, String message, T data, ApiError error) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> failure(String message, String code, String detail) {
        return new ApiResponse<>(false, message, null, new ApiError(code, detail));
    }
}
