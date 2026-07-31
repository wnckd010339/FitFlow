package com.acorn.gymmanagement.member.dto.response;

import com.acorn.gymmanagement.member.model.MemberStatus;

import java.time.LocalDateTime;

public record MemberListResponse(
        Long id,
        String name,
        String loginId,
        String phone,
        MemberStatus status,
        String membershipName,
        Integer remainingDays,
        String trainerName,
        LocalDateTime lastAttendanceAt
) {
}
