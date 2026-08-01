package com.acorn.gymmanagement.member.dto.response;

public record CreateMemberResponse(
        Long memberId,
        Long userId,
        String loginId,
        String name,
        String status
) {
}
