package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TrainerWorkoutForm(
        @NotNull(message = "회원을 선택해 주세요.") Long memberId,
        Long routineId,
        @NotBlank(message = "운동 종목을 입력해 주세요.") String exerciseName,
        @NotNull @Min(1) @Max(20) Integer sets,
        @Min(0) BigDecimal weight,
        @Min(0) @Max(1000) Integer reps,
        @NotNull @Min(1) @Max(1440) Integer durationMinutes,
        String memo
) { }
