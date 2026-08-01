package com.acorn.gymmanagement.member.dto.request;

import com.acorn.gymmanagement.member.model.MemberGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMemberRequest(
        @NotBlank(message = "회원 이름을 입력해 주세요.")
        @Size(max = 100, message = "회원 이름은 100자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "연락처를 입력해 주세요.")
        @Pattern(regexp = "^01[016789]-?\\d{3,4}--?\\d{4}$",
                    message = "올바른 휴대전화 번호를 입력해 주세요.")
        String phone,

        @Past(message = "생년월일은 오늘보다 이전이어야 합니다.")
        LocalDate birthDate,

        MemberGender gender,

        @NotBlank(message = "로그인 ID를 입력해 주세요.")
        @Size(min = 4, max = 100, message = "로그인 ID는 4자 이상 100자 이하로 입력해 주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
                 message = "로그인 ID는 영문, 숫자, 마침표, 밑줄, 하이픈만 사용할 수 있습니다.")
        String loginId,

        @NotBlank(message = "초기 비밀번호를 입력해 주세요.")
        @Size(min = 8, max = 100, message = "초기 비밀번호는 8자 이상 입력해 주세요.")
        String initialPassword,

        boolean trainerRequested
) {
}
