package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineExerciseView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineView;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainerRoutineMapper {
    List<TrainerRoutineView> findRoutines(@Param("userId") Long userId, @Param("memberId") Long memberId);
    Optional<TrainerRoutineView> findRoutine(@Param("userId") Long userId, @Param("routineId") Long routineId);
    List<TrainerRoutineExerciseView> findRoutineExercises(@Param("routineId") Long routineId);
    boolean existsRoutineForMember(
            @Param("userId") Long userId, @Param("routineId") Long routineId,
            @Param("memberId") Long memberId);
    int insertRoutine(TrainerRoutineRegistration registration);
    int updateRoutine(TrainerRoutineRegistration registration);
    int cancelRoutine(@Param("userId") Long userId, @Param("routineId") Long routineId);
    int deleteRoutineExercises(@Param("routineId") Long routineId);
    int insertRoutineExercise(TrainerRoutineExerciseRegistration registration);
}
