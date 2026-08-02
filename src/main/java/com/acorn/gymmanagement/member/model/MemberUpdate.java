package com.acorn.gymmanagement.member.model;

import java.time.LocalDate;

public record MemberUpdate(
        Long memberId,
        String name,
        String phone,
        LocalDate birthDate,
        MemberGender gender,
        MemberStatus status
) {
}
