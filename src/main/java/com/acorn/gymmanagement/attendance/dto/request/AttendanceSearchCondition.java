package com.acorn.gymmanagement.attendance.dto.request;

import java.time.LocalDate;

public record AttendanceSearchCondition(String keyword, LocalDate date, String status) {
    public AttendanceSearchCondition {
        keyword = keyword == null ? null : keyword.trim();
        status = status == null ? null : status.trim();
    }

    public LocalDate searchDate() {
        return date == null ? LocalDate.now() : date;
    }
}
