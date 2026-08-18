package com.acorn.gymmanagement.mypage.dto.response;
import java.time.LocalDate;
public record MemberWorkoutDayView(LocalDate workoutDate,String workoutSummary,Integer exerciseCount,Integer totalSets,Long firstSessionId) {}
