package com.acorn.gymmanagement.mypage.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WorkoutRecordForm(
        Long routineId,
        @NotBlank(message = "운동 종목을 입력해 주세요.") String exerciseName,
        @NotNull(message = "세트 수를 입력해 주세요.") @Min(value = 1, message = "세트 수는 1 이상이어야 합니다.") @Max(value = 20, message = "세트 수는 20 이하여야 합니다.") Integer sets,
        @Min(value = 0, message = "중량은 0 이상이어야 합니다.") BigDecimal weight,
        @Min(value = 0, message = "횟수는 0 이상이어야 합니다.") @Max(value = 1000, message = "횟수는 1000 이하여야 합니다.") Integer reps,
        @NotNull(message = "운동 시간을 입력해 주세요.") @Min(value = 1, message = "운동 시간은 1분 이상이어야 합니다.") @Max(value = 1440, message = "운동 시간은 24시간 이하여야 합니다.") Integer durationMinutes,
        String memo
) {}
