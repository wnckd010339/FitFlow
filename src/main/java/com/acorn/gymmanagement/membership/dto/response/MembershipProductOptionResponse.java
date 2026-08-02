package com.acorn.gymmanagement.membership.dto.response;

import com.acorn.gymmanagement.membership.model.MembershipProductType;

import java.math.BigDecimal;

public record MembershipProductOptionResponse(
        Long productId,
        String name,
        MembershipProductType productType,
        Integer durationDays,
        BigDecimal price,
        Integer ptSessionCount
) {
}
