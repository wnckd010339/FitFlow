package com.acorn.gymmanagement.attendance.dto.response;

public record AttendanceSummaryResponse(int checkedInCount, int currentCount, int checkedOutCount, int missingCheckoutCount) {
}
