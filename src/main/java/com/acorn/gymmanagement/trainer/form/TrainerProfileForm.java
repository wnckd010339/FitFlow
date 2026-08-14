package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TrainerProfileForm(
        @NotBlank(message = "이름을 입력해 주세요.") String name,
        @NotBlank(message = "연락처를 입력해 주세요.") @Pattern(regexp = "^[0-9-]{10,13}$", message = "연락처 형식이 올바르지 않습니다.") String phone,
        String specialty
) { }
