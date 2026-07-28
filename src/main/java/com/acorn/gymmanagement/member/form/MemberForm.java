package com.acorn.gymmanagement.member.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotBlank(message = "회원 이름을 입력해 주세요.")
    private String name;

    @NotBlank(message = "연락처를 입력해 주세요.")
    private String phone;
}
