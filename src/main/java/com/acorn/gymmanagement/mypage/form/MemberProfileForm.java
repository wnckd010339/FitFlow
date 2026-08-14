package com.acorn.gymmanagement.mypage.form;

import com.acorn.gymmanagement.member.model.MemberGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MemberProfileForm(
        @NotBlank(message = "이름을 입력해 주세요.")
        @Size(max = 100, message = "이름은 100자 이하로 입력해 주세요.") String name,
        @NotBlank(message = "연락처를 입력해 주세요.")
        @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "올바른 휴대전화 번호를 입력해 주세요.") String phone,
        @Past(message = "생년월일은 오늘보다 이전이어야 합니다.") LocalDate birthDate,
        MemberGender gender,
        @Email(message = "올바른 이메일 주소를 입력해 주세요.")
        @Size(max = 255, message = "이메일은 255자 이하로 입력해 주세요.") String email
) {
}
