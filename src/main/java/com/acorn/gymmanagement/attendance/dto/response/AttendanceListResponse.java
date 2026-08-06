package com.acorn.gymmanagement.attendance.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceListResponse(Long attendanceId, Long memberId, String memberName, String phone,
                                     String membershipName, LocalDate attendanceDate,
                                     LocalDateTime checkedInAt, LocalDateTime checkedOutAt,
                                     Long durationMinutes, String status) {
}
