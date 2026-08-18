package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerPortalControllerTest {

    @Test
    void editFormPreservesExistingWorkoutValues() {
        LocalDateTime startedAt = LocalDateTime.of(2026, 8, 18, 14, 0);
        TrainerWorkoutView workout = new TrainerWorkoutView(
                100L, 200L, "홍길동", 300L, "하체 루틴",
                startedAt, startedAt.plusMinutes(75), "스쿼트", 4,
                new BigDecimal("80.50"), 10, "마지막 세트 집중"
        );

        TrainerWorkoutForm form = TrainerPortalController.toWorkoutForm(workout);

        assertEquals(200L, form.memberId());
        assertEquals(300L, form.routineId());
        assertEquals("스쿼트", form.exerciseName());
        assertEquals(4, form.sets());
        assertEquals(new BigDecimal("80.50"), form.weight());
        assertEquals(10, form.reps());
        assertEquals(75, form.durationMinutes());
        assertEquals("마지막 세트 집중", form.memo());
    }
}
