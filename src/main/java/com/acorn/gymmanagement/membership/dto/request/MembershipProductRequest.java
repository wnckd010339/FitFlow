package com.acorn.gymmanagement.membership.dto.request;

import com.acorn.gymmanagement.membership.model.MembershipProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MembershipProductRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull MembershipProductType productType,
        @NotNull @Min(1) Integer durationDays,
        @NotNull @DecimalMin("0") BigDecimal price,
        @NotNull @Min(0) Integer ptSessionCount,
        @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE") String status
) {
    public MembershipProductRequest {
        name = name == null ? null : name.trim();
    }
}
