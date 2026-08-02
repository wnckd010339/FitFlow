package com.acorn.gymmanagement.member.dto.request;

import com.acorn.gymmanagement.member.model.MemberGender;
import com.acorn.gymmanagement.member.model.MemberStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateMemberRequest(
        @NotBlank(message = "회원 이름을 입력해 주세요.")
        @Size(max = 100, message = "회원 이름은 100자 이하로ㅗ 입력해 주세요.")
        String name,

        @NotBlank(message = "연락처를 입력해 주세요.")
        @Pattern(
                regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "올바른 휴대전화 번호를 입력해 주세요."
        )
        String phone,

        @Past(message = "생년월일은 오늘보다 이전이어야 합니다.")
        LocalDate birthDate,
        MemberGender gender,

        @NotNull(message = "회원 상태를 선택해 주세요.")
        MemberStatus status

) {
}
