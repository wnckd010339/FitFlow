package com.acorn.gymmanagement.workout.model;

import java.time.LocalDateTime;

public class WorkoutSessionRegistration {
    private Long sessionId;
    private final Long memberId;
    private final Long routineId;
    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final String memo;

    public WorkoutSessionRegistration(
            Long memberId, Long routineId, LocalDateTime startedAt,
            LocalDateTime endedAt, String memo) {
        this(null, memberId, routineId, startedAt, endedAt, memo);
    }

    public WorkoutSessionRegistration(
            Long sessionId, Long memberId, Long routineId, LocalDateTime startedAt,
            LocalDateTime endedAt, String memo) {
        this.sessionId = sessionId;
        this.memberId = memberId;
        this.routineId = routineId;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.memo = memo;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getMemberId() { return memberId; }
    public Long getRoutineId() { return routineId; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public String getMemo() { return memo; }
}
