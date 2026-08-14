package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDateTime;
public record TrainerWorkoutView(Long sessionId, Long memberId, String memberName, String routineTitle,
        LocalDateTime startedAt, LocalDateTime endedAt, String exerciseName, Integer sets, String memo) { }
