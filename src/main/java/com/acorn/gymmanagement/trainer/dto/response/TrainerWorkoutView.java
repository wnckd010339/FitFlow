package com.acorn.gymmanagement.trainer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrainerWorkoutView(
        Long sessionId,
        Long memberId,
        String memberName,
        Long routineId,
        String routineTitle,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        String exerciseName,
        Integer sets,
        BigDecimal weight,
        Integer reps,
        String memo
) { }
