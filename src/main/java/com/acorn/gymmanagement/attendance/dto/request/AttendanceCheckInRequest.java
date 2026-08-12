package com.acorn.gymmanagement.attendance.dto.request;

import jakarta.validation.constraints.NotNull;

public record AttendanceCheckInRequest(
        @NotNull(message = "회원권을 선택해 주세요.")
        Long memberId
) {
}
