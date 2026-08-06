package com.acorn.gymmanagement.payment.dto.response;

import com.acorn.gymmanagement.payment.model.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundResponse(
        Long refundId,
        Long paymentId,
        BigDecimal amount,
        String reason,
        RefundStatus status,
        LocalDateTime refundedAt,
        Long processedBy
) {
}
