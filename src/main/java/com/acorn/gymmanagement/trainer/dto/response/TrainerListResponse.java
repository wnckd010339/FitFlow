package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDate;

public record TrainerListResponse(Long trainerId, String name, String phone, String specialty,
                                  int assignedMemberCount, String status, LocalDate latestAssignedAt) { }
