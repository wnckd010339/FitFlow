package com.acorn.gymmanagement.payment.model;

import com.acorn.gymmanagement.membership.model.MembershipStatus;

public record ExpiredPaymentOrderTarget(
        Long paymentOrderId,
        Long memberId,
        Long membershipId,
        MembershipStatus membershipStatus
) {
}
