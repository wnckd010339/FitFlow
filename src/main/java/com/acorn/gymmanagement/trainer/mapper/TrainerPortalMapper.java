package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.response.*;
import com.acorn.gymmanagement.trainer.form.TrainerProfileForm;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface TrainerPortalMapper {
    Optional<Long> findTrainerId(@Param("userId") Long userId);
    boolean existsAssignedMember(@Param("trainerId") Long trainerId, @Param("memberId") Long memberId);
    TrainerProfileView findProfile(@Param("userId") Long userId);
    int updateProfile(@Param("userId") Long userId, @Param("form") TrainerProfileForm form);
    List<TrainerMemberView> findMembers(@Param("userId") Long userId, @Param("keyword") String keyword);
    Optional<TrainerMemberDetailView> findMemberDetail(@Param("userId") Long userId, @Param("memberId") Long memberId);
    List<TrainerAttendanceView> findAttendances(@Param("memberId") Long memberId);
    List<TrainerWorkoutView> findWorkouts(@Param("userId") Long userId, @Param("memberId") Long memberId);
    List<TrainerWorkoutDayView> findWorkoutDays(@Param("userId") Long userId, @Param("memberId") Long memberId);
    List<TrainerWorkoutView> findWorkoutsByDate(@Param("userId") Long userId,
                                                @Param("memberId") Long memberId,
                                                @Param("workoutDate") java.time.LocalDate workoutDate);
    List<TrainerRoutineView> findRoutines(@Param("userId") Long userId, @Param("memberId") Long memberId);
    Optional<TrainerRoutineView> findRoutine(@Param("userId") Long userId, @Param("routineId") Long routineId);
    List<TrainerRoutineExerciseView> findRoutineExercises(@Param("routineId") Long routineId,
                                                          @Param("workoutGroupId") Long workoutGroupId);
    int insertRoutine(TrainerRoutineRegistration registration);
    int updateRoutine(TrainerRoutineRegistration registration);
    int cancelRoutine(@Param("userId") Long userId, @Param("routineId") Long routineId);
    int deleteRoutineExercises(@Param("routineId") Long routineId);
    int insertRoutineExercise(TrainerRoutineExerciseRegistration registration);
    Optional<TrainerWorkoutView> findWorkout(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    List<TrainerWorkoutExerciseView> findWorkoutExercises(@Param("userId") Long userId,
                                                          @Param("sessionId") Long sessionId);
    int insertWorkoutSession(com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration registration);
    int updateWorkoutSession(com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration registration);
    int deleteWorkoutSets(@Param("sessionId") Long sessionId);
    int deleteWorkoutSession(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    int deleteWorkoutSetsByDate(@Param("userId") Long userId, @Param("memberId") Long memberId,
                                @Param("workoutDate") java.time.LocalDate workoutDate);
    int deleteWorkoutSessionsByDate(@Param("userId") Long userId, @Param("memberId") Long memberId,
                                    @Param("workoutDate") java.time.LocalDate workoutDate);
    int insertWorkoutSet(com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration registration);
}
