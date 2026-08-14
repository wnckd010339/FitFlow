package com.acorn.gymmanagement.trainer.dto.response;

public record TrainerHomeProfileResponse(
        String name,
        String specialty,
        int assignedMemberCount,
        int activeRoutineCount,
        int weeklyWorkoutCount,
        int membersWithRecentWorkoutCount
) { }
