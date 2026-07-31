package com.acorn.gymmanagement.member.dto.response;

import java.time.LocalDateTime;

public record MemberActivityResponse(
        String activityType,
        String description,
        LocalDateTime occurredAt
) {
}
