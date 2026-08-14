package com.acorn.gymmanagement.trainer.dto.response;
import java.math.BigDecimal;
public record TrainerRoutineExerciseView(Long exerciseId,String exerciseName,Integer displayOrder,
        Integer targetSets,Integer targetRepsMin,Integer targetRepsMax,BigDecimal targetWeight,
        Integer restSeconds,String memo) { }
