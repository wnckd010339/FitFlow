package com.acorn.gymmanagement.workout.model;

import java.math.BigDecimal;

public record WorkoutSetRegistration(
        Long sessionId, String exerciseName, Integer setNumber,
        BigDecimal weight, Integer reps) { }
