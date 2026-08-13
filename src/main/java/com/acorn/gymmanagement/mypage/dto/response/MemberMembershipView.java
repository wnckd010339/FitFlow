package com.acorn.gymmanagement.mypage.dto.response;

import java.time.LocalDate;

public record MemberMembershipView(
        Long membershipId, String productName, String productType, LocalDate startDate,
        LocalDate endDate, Integer remainingPtSessions, String status, Integer remainingDays
) {}
