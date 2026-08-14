package com.acorn.gymmanagement.trainer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
public record TrainerRoutineView(Long routineId, Long memberId, String memberName, String title, String description,
        LocalDate startDate, LocalDate endDate, String exerciseName, Integer targetSets, Integer targetRepsMin,
        Integer targetRepsMax, BigDecimal targetWeight, Integer restSeconds, Integer dayOfWeek) { }
