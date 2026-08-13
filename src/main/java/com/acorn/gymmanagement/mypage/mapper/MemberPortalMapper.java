package com.acorn.gymmanagement.mypage.mapper;

import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberPortalMapper {
    Optional<Long> findActiveMemberId(@Param("userId") Long userId);
    List<MemberMembershipView> findMemberships(@Param("userId") Long userId);
    List<MemberAttendanceView> findAttendances(@Param("userId") Long userId);
    List<MemberRoutineView> findActiveRoutine(@Param("userId") Long userId);
    List<MemberWorkoutView> findWorkouts(@Param("userId") Long userId);
    Optional<MemberWorkoutEditView> findWorkoutForEdit(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    List<MemberPaymentView> findPayments(@Param("userId") Long userId);
    int insertWorkoutSession(WorkoutSessionRegistration registration);
    int insertWorkoutSet(WorkoutSetRegistration registration);
    int updateWorkoutSession(WorkoutSessionRegistration registration);
    int deleteWorkoutSets(@Param("sessionId") Long sessionId);
}
