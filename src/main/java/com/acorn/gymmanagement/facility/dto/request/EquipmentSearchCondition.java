package com.acorn.gymmanagement.facility.dto.request;

public record EquipmentSearchCondition(String keyword, String category, String status) {
    public EquipmentSearchCondition {
        keyword = keyword == null ? null : keyword.trim();
        category = category == null ? null : category.trim();
        status = status == null ? null : status.trim();
    }
}
