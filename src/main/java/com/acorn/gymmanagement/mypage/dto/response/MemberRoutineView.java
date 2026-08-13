package com.acorn.gymmanagement.mypage.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MemberRoutineView(Long routineId, String title, String description, LocalDate startDate,
                                LocalDate endDate, String exerciseName, Integer targetSets,
                                Integer targetRepsMin, Integer targetRepsMax, BigDecimal targetWeight,
                                Integer dayOfWeek, Integer displayOrder) {}
