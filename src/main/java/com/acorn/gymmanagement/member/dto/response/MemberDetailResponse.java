package com.acorn.gymmanagement.member.dto.response;

import com.acorn.gymmanagement.member.model.MemberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberDetailResponse(
        Long id,
        String name,
        String loginId,
        String phone,
        LocalDate birthDate,
        LocalDate joinedAt,
        MemberStatus status,
        LocalDateTime lastLoginAt
) {
}
