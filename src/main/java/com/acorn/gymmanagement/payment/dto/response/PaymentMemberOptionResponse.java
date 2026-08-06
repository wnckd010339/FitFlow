package com.acorn.gymmanagement.payment.dto.response;

public record PaymentMemberOptionResponse(
        Long memberId,
        String name,
        String phone
) {
}
