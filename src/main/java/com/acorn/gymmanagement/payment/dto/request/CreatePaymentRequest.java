package com.acorn.gymmanagement.payment.dto.request;

import com.acorn.gymmanagement.payment.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotNull(message = "결제수단을 선택해 주세요.")
        PaymentMethod paymentMethod
) {
}
