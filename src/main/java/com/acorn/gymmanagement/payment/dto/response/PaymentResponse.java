package com.acorn.gymmanagement.payment.dto.response;

import com.acorn.gymmanagement.payment.model.PaymentMethod;
import com.acorn.gymmanagement.payment.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long memberId,
        Long membershipId,
        String memberName,
        String productName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDateTime paidAt,
        BigDecimal refundedAmount,
        BigDecimal refundableAmount
) {
}
