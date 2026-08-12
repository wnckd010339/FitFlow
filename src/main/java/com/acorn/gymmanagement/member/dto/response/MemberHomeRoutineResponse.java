package com.acorn.gymmanagement.member.dto.response;

public record MemberHomeRoutineResponse(
        Long routineId,
        String title,
        String description
) {
}
