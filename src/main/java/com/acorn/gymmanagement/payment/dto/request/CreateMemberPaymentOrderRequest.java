package com.acorn.gymmanagement.payment.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateMemberPaymentOrderRequest(
        @NotNull Long productId,
        @NotNull @FutureOrPresent LocalDate startDate
        ) {
}
