package com.acorn.gymmanagement.dashboard.mapper;

import com.acorn.gymmanagement.dashboard.dto.response.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardMapper {
    DashboardSummaryResponse findSummary();
    List<HourlyAttendanceCount> findHourlyAttendance();
    List<DashboardActivityResponse> findRecentActivities();
    List<ExpiringMembershipResponse> findExpiringMemberships();
}
