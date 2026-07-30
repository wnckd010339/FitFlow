package com.acorn.gymmanagement.auth.model;

public record LocalAuthenticatedUser(
        Long userId,
        String loginId,
        String passwordHash,
        String email,
        String role,
        String status
) {
}
