package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDate;

public record TrainerWorkoutDayView(
        Long memberId,
        String memberName,
        LocalDate workoutDate,
        String workoutSummary,
        Integer exerciseCount,
        Integer totalSets,
        Long firstSessionId
) { }
