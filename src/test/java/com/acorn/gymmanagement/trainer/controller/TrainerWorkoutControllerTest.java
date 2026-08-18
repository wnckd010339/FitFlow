package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerWorkoutControllerTest {
    @Test
    void editFormPreservesAllExistingWorkoutExercises() {
        LocalDateTime startedAt = LocalDateTime.of(2026, 8, 18, 14, 0);
        TrainerWorkoutView workout = new TrainerWorkoutView(
                100L, 200L, "홍길동", 300L, "하체 루틴",
                startedAt, startedAt.plusMinutes(75), "스쿼트", 7,
                new BigDecimal("80.50"), 10, "마지막 세트 집중");
        List<TrainerWorkoutExerciseForm> exercises = List.of(
                new TrainerWorkoutExerciseForm("스쿼트", 4, new BigDecimal("80.50"), 10),
                new TrainerWorkoutExerciseForm("레그 프레스", 3, new BigDecimal("120.00"), 12));

        TrainerWorkoutForm form = TrainerWorkoutController.toWorkoutForm(workout, exercises);

        assertEquals(200L, form.getMemberId());
        assertEquals(300L, form.getRoutineId());
        assertEquals(75, form.getDurationMinutes());
        assertEquals("마지막 세트 집중", form.getMemo());
        assertEquals(2, form.getExercises().size());
        assertEquals("레그 프레스", form.getExercises().get(1).getExerciseName());
        assertEquals(new BigDecimal("120.00"), form.getExercises().get(1).getWeight());
    }
}

