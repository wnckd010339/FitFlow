package com.acorn.gymmanagement.membership.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateMemberMembershipRequest(
        @NotNull(message = "회원권 상품을 선택해 주세요.")
        Long productId,

        @NotNull(message = "회원권 시작일을 입력해 주세요.")
        LocalDate startDate
) {
}
