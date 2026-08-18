package com.acorn.gymmanagement.trainer.dto.response;

import java.math.BigDecimal;

public record TrainerWorkoutExerciseView(
        String exerciseName,
        Integer sets,
        BigDecimal weight,
        Integer reps
) { }
