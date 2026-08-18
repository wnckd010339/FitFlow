package com.acorn.gymmanagement.mypage.dto.response;
import java.math.BigDecimal;
public record MemberWorkoutExerciseView(Long sessionId,String exerciseName,Integer sets,BigDecimal weight,Integer reps) {}
