package com.acorn.gymmanagement.mypage.dto.response;

import java.time.Duration;
import java.time.LocalDateTime;

public record MemberWorkoutEditView(Long sessionId, Long routineId, LocalDateTime startedAt,
                                    LocalDateTime endedAt, String memo) {
    public int durationMinutes() {
        if (startedAt == null || endedAt == null) return 60;
        long minutes = Duration.between(startedAt, endedAt).toMinutes();
        return minutes > 0 ? (int) minutes : 60;
    }
}
