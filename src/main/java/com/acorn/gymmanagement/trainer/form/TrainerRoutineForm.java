package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TrainerRoutineForm(
        @NotNull(message = "회원을 선택해 주세요.") Long memberId,
        @NotBlank(message = "루틴 제목을 입력해 주세요.") String title,
        String description,
        @NotNull(message = "시작일을 입력해 주세요.") LocalDate startDate,
        LocalDate endDate,
        @NotBlank(message = "운동 종목을 입력해 주세요.") String exerciseName,
        @NotNull @Min(1) @Max(20) Integer targetSets,
        @Min(0) Integer targetRepsMin,
        @Min(0) Integer targetRepsMax,
        @Min(0) BigDecimal targetWeight,
        @Min(0) @Max(3600) Integer restSeconds,
        @Min(1) @Max(7) Integer dayOfWeek
) { }
