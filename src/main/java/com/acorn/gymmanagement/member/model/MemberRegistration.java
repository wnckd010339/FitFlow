package com.acorn.gymmanagement.member.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class MemberRegistration {

    @Setter
    private Long userId;

    @Setter
    private Long memberId;

    private final String loginId;
    private final String passwordHash;
    private final String name;
    private final String phone;
    private final LocalDate birthDate;
    private final MemberGender gender;
    private final boolean trainerRequested;

    public MemberRegistration(
            String loginId,
            String passwordHash,
            String name,
            String phone,
            LocalDate birthDate,
            MemberGender gender,
            boolean trainerRequested
    ) {
        this.loginId = loginId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.trainerRequested = trainerRequested;
    }

}
