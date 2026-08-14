package com.acorn.gymmanagement.auth.form;

import com.acorn.gymmanagement.member.dto.request.CreateMemberRequest;
import com.acorn.gymmanagement.member.model.MemberGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

public class SignupForm {
    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 100, message = "이름은 100자 이하로 입력해 주세요.")
    private String name;

    @NotBlank(message = "연락처를 입력해 주세요.")
    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "올바른 휴대전화 번호를 입력해 주세요.")
    private String phone;

    @NotNull(message = "생년월일을 입력해 주세요.")
    @Past(message = "생년월일은 오늘보다 이전이어야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull(message = "성별을 선택해 주세요.")
    private MemberGender gender;

    @NotBlank(message = "로그인 ID를 입력해 주세요.")
    @Size(min = 4, max = 100, message = "로그인 ID는 4자 이상 100자 이하로 입력해 주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "로그인 ID에는 영문, 숫자, 마침표, 밑줄, 하이픈만 사용할 수 있습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 입력해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordConfirmation;

    private boolean trainerRequested;

    public SignupForm() {
    }

    public boolean passwordsMatch() {
        return Objects.equals(password, passwordConfirmation);
    }

    public CreateMemberRequest toCreateMemberRequest() {
        return new CreateMemberRequest(name, phone, birthDate, gender, loginId, password, trainerRequested);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public MemberGender getGender() { return gender; }
    public void setGender(MemberGender gender) { this.gender = gender; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordConfirmation() { return passwordConfirmation; }
    public void setPasswordConfirmation(String passwordConfirmation) { this.passwordConfirmation = passwordConfirmation; }
    public boolean isTrainerRequested() { return trainerRequested; }
    public void setTrainerRequested(boolean trainerRequested) { this.trainerRequested = trainerRequested; }
}
