package com.acorn.gymmanagement.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(
        @NotBlank(message = "회원 이름을 입력해 주세요.") String name,
        @NotBlank(message = "연락처를 입력해 주세요.") String phone
) {
}
