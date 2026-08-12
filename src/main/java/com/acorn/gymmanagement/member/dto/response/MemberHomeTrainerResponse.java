package com.acorn.gymmanagement.member.dto.response;

public record MemberHomeTrainerResponse(
        Long trainerId,
        String trainerName,
        String specialty
) {
}
