package com.acorn.gymmanagement.dashboard.dto.response;

import java.math.BigDecimal;

public record DashboardSummaryResponse(int todayCheckInCount, int yesterdayCheckInCount,
        int activeMembershipCount, int newMemberCount, BigDecimal monthlySales,
        int expiringMembershipCount, int currentCount, int checkedOutCount, int alertCount) { }
