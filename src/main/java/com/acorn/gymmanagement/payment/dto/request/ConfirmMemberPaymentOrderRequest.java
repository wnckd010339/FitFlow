package com.acorn.gymmanagement.payment.dto.request;

import com.acorn.gymmanagement.payment.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ConfirmMemberPaymentOrderRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull PaymentMethod paymentMethod
) { }
