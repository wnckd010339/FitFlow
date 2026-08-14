package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDateTime;

public record TrainerHomeMemberResponse(
        Long memberId,
        String memberName,
        String routineTitle,
        LocalDateTime lastWorkoutAt
) { }
