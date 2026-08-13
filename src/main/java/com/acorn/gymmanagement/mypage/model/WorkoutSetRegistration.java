package com.acorn.gymmanagement.mypage.model;

import java.math.BigDecimal;

public record WorkoutSetRegistration(Long sessionId, String exerciseName, Integer setNumber,
                                     BigDecimal weight, Integer reps) {}
