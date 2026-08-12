package com.acorn.gymmanagement.member.dto.response;

import java.time.LocalDate;

public record MemberHomeSummaryResponse(
        Long memberId,
        String memberName,
        int weeklyWorkoutCount,
        int monthlyAttendanceCount,
        int previousMonthAttendanceCount,
        String membershipName,
        LocalDate membershipEndDate,
        Integer remainingDays,
        int remainingPtSessions
) {
}
