package com.acorn.gymmanagement.trainer.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AssignTrainerRequest(
        @NotNull Long memberId,
        @NotNull Long trainerId,
        @NotNull LocalDate startedAt
) { }
