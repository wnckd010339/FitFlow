package com.acorn.gymmanagement.trainer.dto.response;

import com.acorn.gymmanagement.member.model.MemberGender;
import java.time.LocalDate;

public record TrainerMemberDetailView(Long memberId, String name, String phone, String email,
        LocalDate birthDate, MemberGender gender, String activeMembership, Integer remainingPtSessions) { }
