package com.acorn.gymmanagement.dashboard.dto.response;

import java.util.List;

public record DashboardResponse(String dateLabel, DashboardSummaryResponse summary, int checkInChangeRate,
        List<HourlyAttendanceResponse> attendanceChart, List<DashboardActivityResponse> recentActivities,
        List<ExpiringMembershipResponse> expiringMemberships) { }
