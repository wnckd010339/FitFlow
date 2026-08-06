package com.acorn.gymmanagement.attendance.service;

import com.acorn.gymmanagement.attendance.dto.request.AttendanceSearchCondition;
import com.acorn.gymmanagement.attendance.dto.response.AttendanceListResponse;
import com.acorn.gymmanagement.attendance.dto.response.AttendanceSummaryResponse;
import com.acorn.gymmanagement.attendance.mapper.AttendanceMapper;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {
    private final AttendanceMapper attendanceMapper;

    public AttendanceSummaryResponse getSummary(LocalDate date) {
        return attendanceMapper.findSummary(date);
    }

    public List<AttendanceListResponse> findCurrentAttendances(AttendanceSearchCondition condition) {
        return attendanceMapper.findCurrentAttendances(condition);
    }

    public List<AttendanceListResponse> findHistory(AttendanceSearchCondition condition) {
        return attendanceMapper.findHistory(condition);
    }

    @Transactional
    public void checkout(Long attendanceId) {
        attendanceMapper.findOpenAttendanceForUpdate(attendanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "퇴실 처리할 입장 기록을 찾을 수 없습니다."));
        if (attendanceMapper.checkout(attendanceId, LocalDateTime.now()) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "출석 상태가 변경되어 퇴실 처리하지 못했습니다.");
        }
    }
}
