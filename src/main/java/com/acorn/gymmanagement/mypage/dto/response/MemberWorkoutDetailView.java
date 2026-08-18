package com.acorn.gymmanagement.mypage.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record MemberWorkoutDetailView(Long sessionId,String routineTitle,LocalDateTime startedAt,LocalDateTime endedAt,String exerciseName,Integer sets,BigDecimal weight,Integer reps,String memo) {}
