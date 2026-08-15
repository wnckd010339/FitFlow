package com.acorn.gymmanagement.payment.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentOrderRegistration {

    private Long id;

    private final String orderId;
    private final Long memberId;
    private final Long membershipId;
    private final String pgProvider;
    private final BigDecimal amount;
    private final PaymentOrderStatus status;
    private final String idempotencyKey;
    private final LocalDateTime expiresAt;

    public PaymentOrderRegistration(
            String orderId,
            Long memberId,
            Long membershipId,
            String pgProvider,
            BigDecimal amount,
            PaymentOrderStatus status,
            String idempotencyKey,
            LocalDateTime expiresAt
    ){
       this.orderId = orderId;
       this.memberId = memberId;
       this.membershipId = membershipId;
       this.pgProvider = pgProvider;
       this.amount = amount;
       this.status = status;
       this.idempotencyKey = idempotencyKey;
       this.expiresAt = expiresAt;
    }

    public void setId(Long id){
        this.id = id;
    }
}
