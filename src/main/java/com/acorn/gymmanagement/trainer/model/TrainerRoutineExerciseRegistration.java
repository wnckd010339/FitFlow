package com.acorn.gymmanagement.trainer.model;
import java.math.BigDecimal;
public record TrainerRoutineExerciseRegistration(Long routineId,String exerciseName,
        Integer displayOrder,Integer targetSets,Integer targetRepsMin,Integer targetRepsMax,
        BigDecimal targetWeight,Integer restSeconds,String memo) { }
