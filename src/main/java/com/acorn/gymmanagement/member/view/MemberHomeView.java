package com.acorn.gymmanagement.member.view;

import com.acorn.gymmanagement.member.dto.response.MemberHomeAttendanceResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeExerciseResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeRoutineResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeSummaryResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeTrainerResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeWorkoutResponse;

import java.time.LocalDate;
import java.util.List;

public record MemberHomeView(
        LocalDate today,
        int weeklyWorkoutGoal,
        MemberHomeSummaryResponse summary,
        MemberHomeAttendanceResponse attendance,
        MemberHomeTrainerResponse trainer,
        MemberHomeRoutineResponse routine,
        List<MemberHomeExerciseResponse> exercises,
        List<MemberHomeWorkoutResponse> recentWorkouts
) {
    public int attendanceDifference() {
        return summary.monthlyAttendanceCount() - summary.previousMonthAttendanceCount();
    }
}
