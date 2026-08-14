package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDateTime;

public record TrainerMemberView(Long memberId, String name, String phone, String activeMembership,
                                LocalDateTime lastAttendanceAt, LocalDateTime lastWorkoutAt) { }
