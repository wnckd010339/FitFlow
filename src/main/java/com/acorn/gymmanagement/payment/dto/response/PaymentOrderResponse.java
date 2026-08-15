package com.acorn.gymmanagement.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentOrderResponse(
        String orderId,
        String orderName,
        BigDecimal amount,
        LocalDateTime expiresAt
) {
}
