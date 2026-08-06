package com.acorn.gymmanagement.payment.dto.response;

import com.acorn.gymmanagement.membership.model.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentTargetResponse(
        Long membershipId,
        Long memberId,
        String memberName,
        String memberPhone,
        Long productId,
        String productName,
        BigDecimal price,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus membershipStatus
) {
}
