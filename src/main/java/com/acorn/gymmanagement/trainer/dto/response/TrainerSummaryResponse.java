package com.acorn.gymmanagement.trainer.dto.response;

public record TrainerSummaryResponse(int totalCount, int activeCount, int assignedMemberCount, int waitingMemberCount) { }
