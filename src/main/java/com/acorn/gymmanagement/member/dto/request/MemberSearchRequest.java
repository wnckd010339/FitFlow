package com.acorn.gymmanagement.member.dto.request;

import com.acorn.gymmanagement.member.model.MemberStatus;

public record MemberSearchRequest(
        String keyword,
        MemberStatus status,
        String membershipStatus
) {
}
