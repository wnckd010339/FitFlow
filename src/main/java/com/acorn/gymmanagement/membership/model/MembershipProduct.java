package com.acorn.gymmanagement.membership.model;

import java.math.BigDecimal;

public record MembershipProduct(
        Long id,
        String name,
        MembershipProductType productType,
        Integer durationDays,
        BigDecimal price,
        Integer ptSessionCount,
        String status
) {
}
