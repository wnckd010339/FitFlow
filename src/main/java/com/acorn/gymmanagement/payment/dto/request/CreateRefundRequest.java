package com.acorn.gymmanagement.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRefundRequest(
        @NotNull(message = "환불 금액을 입력해 주세요.")
        @DecimalMin(value = "0.01", message = "환불 금액은 0원보다 커야 합니다.")
        BigDecimal amount,

        @Size(max = 500, message = "환불 사유는 500자 이내로 입력해 주세요.")
        String reason
) {
}
