package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
public record TrainerAttendanceView(LocalDate attendanceDate, LocalDateTime checkedInAt, LocalDateTime checkedOutAt) { }
