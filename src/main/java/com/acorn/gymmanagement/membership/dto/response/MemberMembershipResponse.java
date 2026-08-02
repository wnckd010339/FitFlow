package com.acorn.gymmanagement.membership.dto.response;

import com.acorn.gymmanagement.membership.model.MembershipStatus;
import com.acorn.gymmanagement.membership.model.MembershipProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberMembershipResponse(
        Long membershipId,
        Long productId,
        String productName,
        MembershipProductType productType,
        LocalDate startDate,
        LocalDate endDate,
        Integer remainingDays,
        Integer remainingPtSessions,
        MembershipStatus status,
        LocalDateTime createdAt
) {
}
