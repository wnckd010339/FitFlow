package com.acorn.gymmanagement.mypage.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeForm(
        @NotBlank(message = "현재 비밀번호를 입력해 주세요.") String currentPassword,
        @NotBlank(message = "새 비밀번호를 입력해 주세요.")
        @Size(min = 8, max = 72, message = "새 비밀번호는 8~72자로 입력해 주세요.") String newPassword,
        @NotBlank(message = "새 비밀번호 확인을 입력해 주세요.") String newPasswordConfirmation
) { }
