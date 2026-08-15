package com.acorn.gymmanagement.membership.model;

import java.math.BigDecimal;

public record PendingMembershipPaymentTarget(
        Long memberId,
        Long membershipId,
        String productName,
        BigDecimal price
) {
}
