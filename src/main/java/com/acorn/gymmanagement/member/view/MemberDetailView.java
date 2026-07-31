package com.acorn.gymmanagement.member.view;

import com.acorn.gymmanagement.member.dto.response.CurrentMembershipResponse;
import com.acorn.gymmanagement.member.dto.response.MemberActivityResponse;
import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;

import java.util.List;

public record MemberDetailView(
        MemberDetailResponse member,
        CurrentMembershipResponse currentMembership,
        List<MemberActivityResponse> recentActivities
) {
}
