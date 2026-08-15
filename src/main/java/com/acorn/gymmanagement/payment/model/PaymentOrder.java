package com.acorn.gymmanagement.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentOrder(
        Long id,
        String orderId,
        Long memberId,
        Long membershipId,
        Long paymentId,
        String pgProvider,
        BigDecimal amount,
        PaymentOrderStatus status,
        String paymentKey,
        String idempotencyKey,
        LocalDateTime expiresAt,
        LocalDateTime approvedAt
) {
}
