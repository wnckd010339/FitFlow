package com.acorn.gymmanagement.payment.dto.response;

import com.acorn.gymmanagement.payment.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryResponse(
        Long transactionId,
        Long paymentId,
        Long memberId,
        String memberName,
        String transactionType,
        LocalDateTime occurredAt,
        String productName,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String status
) {
}
