package com.acorn.gymmanagement.mypage.dto.response;

import com.acorn.gymmanagement.member.model.MemberGender;
import java.time.LocalDate;

public record MemberProfileView(
        Long memberId, String loginId, String name, String phone,
        LocalDate birthDate, MemberGender gender, String email, String status,
        Integer remainingDays, int monthlyAttendanceCount, int remainingPtSessions
) {
}
