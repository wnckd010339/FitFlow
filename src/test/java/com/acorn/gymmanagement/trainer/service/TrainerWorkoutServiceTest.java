package com.acorn.gymmanagement.trainer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerRoutineMapper;
import com.acorn.gymmanagement.trainer.mapper.TrainerWorkoutMapper;
import com.acorn.gymmanagement.workout.model.WorkoutSessionRegistration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerWorkoutServiceTest {
    @Mock TrainerWorkoutMapper workoutMapper;
    @Mock TrainerRoutineMapper routineMapper;
    @Mock TrainerAccessValidator accessValidator;

    @Test
    void createWorkoutStoresAllSetsInOneSession() {
        TrainerWorkoutService service = new TrainerWorkoutService(
                workoutMapper, routineMapper, accessValidator);
        when(routineMapper.existsRoutineForMember(10L, 50L, 30L)).thenReturn(true);
        when(workoutMapper.insertWorkoutSession(any())).thenAnswer(invocation -> {
            invocation.<WorkoutSessionRegistration>getArgument(0).setSessionId(40L);
            return 1;
        });
        when(workoutMapper.insertWorkoutSet(any())).thenReturn(1);

        service.createWorkout(10L, workoutForm(50L));

        verify(accessValidator).requireAssignedMember(10L, 30L);
        verify(workoutMapper, times(7)).insertWorkoutSet(any());
    }

    @Test
    void createWorkoutRejectsRoutineOwnedByAnotherMember() {
        TrainerWorkoutService service = new TrainerWorkoutService(
                workoutMapper, routineMapper, accessValidator);
        when(routineMapper.existsRoutineForMember(10L, 50L, 30L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.createWorkout(10L, workoutForm(50L)));

        verify(workoutMapper, times(0)).insertWorkoutSession(any());
    }

    @Test
    void deleteWorkoutDayRemovesSetsBeforeSessions() {
        TrainerWorkoutService service = new TrainerWorkoutService(
                workoutMapper, routineMapper, accessValidator);
        LocalDate date = LocalDate.of(2026, 8, 13);
        when(workoutMapper.deleteWorkoutSessionsByDate(10L, 30L, date)).thenReturn(2);

        service.deleteWorkoutDay(10L, 30L, date);

        InOrder order = inOrder(workoutMapper);
        order.verify(workoutMapper).deleteWorkoutSetsByDate(10L, 30L, date);
        order.verify(workoutMapper).deleteWorkoutSessionsByDate(10L, 30L, date);
    }

    private TrainerWorkoutForm workoutForm(Long routineId) {
        return new TrainerWorkoutForm(
                30L, routineId, 70, "하체와 가슴 운동",
                List.of(
                        new TrainerWorkoutExerciseForm(
                                "백 스쿼트", 4, new BigDecimal("80.00"), 10),
                        new TrainerWorkoutExerciseForm(
                                "벤치 프레스", 3, new BigDecimal("60.00"), 8)));
    }
}
