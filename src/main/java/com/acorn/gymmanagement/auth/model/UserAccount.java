package com.acorn.gymmanagement.auth.model;

import java.time.LocalDateTime;

public record UserAccount(
        Long id,
        String loginId,
        String password,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
