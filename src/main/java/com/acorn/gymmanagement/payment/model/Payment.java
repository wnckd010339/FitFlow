package com.acorn.gymmanagement.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(
        Long paymentId,
        Long memberId,
        Long membershipId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDateTime paidAt
) {
}
