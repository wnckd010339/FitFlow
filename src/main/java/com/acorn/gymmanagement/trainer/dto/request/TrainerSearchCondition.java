package com.acorn.gymmanagement.trainer.dto.request;

public record TrainerSearchCondition(String keyword, String status, Boolean available) {
    public TrainerSearchCondition {
        keyword = keyword == null ? null : keyword.trim();
        status = status == null ? null : status.trim();
    }
}
