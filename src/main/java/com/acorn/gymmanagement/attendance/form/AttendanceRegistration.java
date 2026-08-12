package com.acorn.gymmanagement.attendance.form;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRegistration {

    private Long attendanceId;
    private final Long memberId;
    private final LocalDate attendanceDate;
    private final LocalDateTime checkedInAt;

    public AttendanceRegistration(
            Long memberId,
            LocalDate attendanceDate,
            LocalDateTime checkedInAt
    ) {
        this.memberId = memberId;
        this.attendanceDate = attendanceDate;
        this.checkedInAt = checkedInAt;
    }

    public Long getAttendanceId() { return attendanceId; }
    public Long getMemberId() { return memberId; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public LocalDateTime getCheckedInAt() { return checkedInAt; }
}
