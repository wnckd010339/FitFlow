package com.acorn.gymmanagement.member.dto.response;

import java.time.LocalDateTime;

public record MemberHomeAttendanceResponse(
        LocalDateTime checkedInAt
) {
}
