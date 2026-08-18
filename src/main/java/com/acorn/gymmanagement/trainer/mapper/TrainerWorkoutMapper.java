package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutDayView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutExerciseView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.workout.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.workout.model.WorkoutSetRegistration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainerWorkoutMapper {
    List<TrainerWorkoutDayView> findWorkoutDays(
            @Param("userId") Long userId, @Param("memberId") Long memberId);
    List<TrainerWorkoutView> findWorkoutsByDate(
            @Param("userId") Long userId, @Param("memberId") Long memberId,
            @Param("workoutDate") LocalDate workoutDate);
    Optional<TrainerWorkoutView> findWorkout(
            @Param("userId") Long userId, @Param("sessionId") Long sessionId);
    List<TrainerWorkoutExerciseView> findWorkoutExercises(
            @Param("userId") Long userId, @Param("sessionId") Long sessionId);
    int insertWorkoutSession(WorkoutSessionRegistration registration);
    int updateWorkoutSession(WorkoutSessionRegistration registration);
    int deleteWorkoutSets(@Param("sessionId") Long sessionId);
    int deleteWorkoutSession(@Param("userId") Long userId, @Param("sessionId") Long sessionId);
    int deleteWorkoutSetsByDate(
            @Param("userId") Long userId, @Param("memberId") Long memberId,
            @Param("workoutDate") LocalDate workoutDate);
    int deleteWorkoutSessionsByDate(
            @Param("userId") Long userId, @Param("memberId") Long memberId,
            @Param("workoutDate") LocalDate workoutDate);
    int insertWorkoutSet(WorkoutSetRegistration registration);
}
