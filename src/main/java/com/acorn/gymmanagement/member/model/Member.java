package com.acorn.gymmanagement.member.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Member(
        Long id,
        Long userId,
        String name,
        String phone,
        LocalDate joinedAt,
        MemberStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDate birthDate,
        MemberGender gender,
        boolean trainerRequested
) {
}
