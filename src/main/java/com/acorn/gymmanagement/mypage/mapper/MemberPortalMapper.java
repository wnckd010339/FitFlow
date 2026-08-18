package com.acorn.gymmanagement.mypage.mapper;

import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.form.MemberProfileForm;
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
    List<MemberWorkoutDayView> findWorkoutDays(@Param("userId") Long userId);
    List<MemberWorkoutDetailView> findWorkoutsByDate(@Param("userId") Long userId, @Param("workoutDate") java.time.LocalDate workoutDate);
    Optional<MemberWorkoutEditView> findWorkoutForEdit(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    List<MemberWorkoutExerciseView> findWorkoutExercises(@Param("userId") Long userId,@Param("sessionId") Long sessionId);
    List<MemberPaymentView> findPayments(@Param("userId") Long userId);
    Optional<MemberProfileView> findProfile(@Param("userId") Long userId);
    int updateMemberProfile(@Param("userId") Long userId, @Param("form") MemberProfileForm form);
    int updateUserEmail(@Param("userId") Long userId, @Param("email") String email);
    int insertWorkoutSession(WorkoutSessionRegistration registration);
    int insertWorkoutSet(WorkoutSetRegistration registration);
    int updateWorkoutSession(WorkoutSessionRegistration registration);
    int deleteWorkoutSets(@Param("sessionId") Long sessionId);
    int deleteWorkoutSetsByDate(@Param("userId") Long userId,@Param("workoutDate") java.time.LocalDate workoutDate);
    int deleteWorkoutSessionsByDate(@Param("userId") Long userId,@Param("workoutDate") java.time.LocalDate workoutDate);
}
