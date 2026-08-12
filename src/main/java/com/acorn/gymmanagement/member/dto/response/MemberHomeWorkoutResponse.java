package com.acorn.gymmanagement.member.dto.response;

import java.time.LocalDateTime;
import java.time.Duration;

public record MemberHomeWorkoutResponse(
        Long sessionId,
        String routineTitle,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        int exerciseCount
) {
    public Long durationMinutes() {
        return endedAt == null ? null : Duration.between(startedAt, endedAt).toMinutes();
    }
}
