package com.acorn.gymmanagement.mypage.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberAttendanceView(Long attendanceId, LocalDate attendanceDate,
                                   LocalDateTime checkedInAt, LocalDateTime checkedOutAt) {}
