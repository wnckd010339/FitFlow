package com.acorn.gymmanagement.trainer.model;

import java.time.LocalDate;

public record TrainerAssignmentRegistration(
        Long memberId,
        Long trainerId,
        LocalDate startedAt,
        Long assignedBy
) { }
