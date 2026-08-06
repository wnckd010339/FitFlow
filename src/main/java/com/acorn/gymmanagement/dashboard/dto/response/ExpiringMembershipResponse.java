package com.acorn.gymmanagement.dashboard.dto.response;

import java.time.LocalDate;

public record ExpiringMembershipResponse(Long membershipId, Long memberId, String memberName,
        String productName, LocalDate endDate, int remainingDays) { }
