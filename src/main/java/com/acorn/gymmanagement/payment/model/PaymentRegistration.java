package com.acorn.gymmanagement.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRegistration {

    private Long paymentId;
    private final Long memberId;
    private final Long membershipId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;

    public PaymentRegistration(
            Long memberId,
            Long membershipId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDateTime paidAt
    ) {
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paidAt = paidAt;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getMemberId() { return memberId; }
    public Long getMembershipId() { return membershipId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }
}
