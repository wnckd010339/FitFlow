package com.acorn.gymmanagement.auth.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {

    @NotBlank(message = "로그인 ID를 입력해 주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
