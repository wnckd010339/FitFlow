package com.acorn.gymmanagement.payment.dto.response;

import com.acorn.gymmanagement.payment.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemberPaymentConfirmationResponse(
        String orderId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDateTime approvedAt
) { }
