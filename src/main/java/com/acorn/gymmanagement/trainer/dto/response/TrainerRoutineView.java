package com.acorn.gymmanagement.trainer.dto.response;
import java.time.LocalDate;
public record TrainerRoutineView(Long routineId,Long memberId,String memberName,String title,String description,
        LocalDate startDate,LocalDate endDate,Long workoutGroupId,String workoutGroupTitle,
        Integer weekNumber,Integer dayOfWeek,Integer exerciseCount) { }
