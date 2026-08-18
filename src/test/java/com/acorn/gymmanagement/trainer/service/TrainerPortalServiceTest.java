package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.trainer.form.TrainerRoutineExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerPortalMapper;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerPortalServiceTest {
    @Mock TrainerPortalMapper mapper;

    @Test
    void createRoutineStoresAllExercisesWithoutRequiringWorkoutGroupMigration() {
        TrainerPortalService service = new TrainerPortalService(mapper);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 30L)).thenReturn(true);
        when(mapper.insertRoutine(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration>getArgument(0).setRoutineId(40L);
            return 1;
        });
        when(mapper.insertRoutineExercise(any())).thenReturn(1);

        TrainerRoutineForm form = new TrainerRoutineForm(
                30L, "4주 근력 향상 프로그램", "하체 루틴", LocalDate.of(2026, 8, 14),
                LocalDate.of(2026, 9, 10), "1주차 하체", 1, 1,
                List.of(
                        new TrainerRoutineExerciseForm("백 스쿼트", 4, 8, 10, null, 90, null),
                        new TrainerRoutineExerciseForm("레그 프레스", 4, 10, 12, null, 90, null),
                        new TrainerRoutineExerciseForm("루마니안 데드리프트", 3, 8, 10, null, 90, null)
                ));

        service.createRoutine(10L, form);

        ArgumentCaptor<TrainerRoutineExerciseRegistration> captor =
                ArgumentCaptor.forClass(TrainerRoutineExerciseRegistration.class);
        verify(mapper, times(3)).insertRoutineExercise(captor.capture());
        assertEquals(List.of(1, 2, 3), captor.getAllValues().stream().map(TrainerRoutineExerciseRegistration::displayOrder).toList());
        assertEquals(java.util.Arrays.asList(null, null, null), captor.getAllValues().stream().map(TrainerRoutineExerciseRegistration::workoutGroupId).toList());
    }

    @Test
    void createWorkoutStoresEveryExerciseInOneSession() {
        TrainerPortalService service = new TrainerPortalService(mapper);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 30L)).thenReturn(true);
        when(mapper.insertWorkoutSession(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration>getArgument(0).setSessionId(40L);
            return 1;
        });
        when(mapper.insertWorkoutSet(any())).thenReturn(1);
        TrainerWorkoutForm form = new TrainerWorkoutForm(
                30L, 50L, 70, "하체와 가슴 운동",
                List.of(
                        new TrainerWorkoutExerciseForm("백 스쿼트", 4, new BigDecimal("80.00"), 10),
                        new TrainerWorkoutExerciseForm("벤치 프레스", 3, new BigDecimal("60.00"), 8)));

        service.createWorkout(10L, form);

        ArgumentCaptor<WorkoutSetRegistration> captor = ArgumentCaptor.forClass(WorkoutSetRegistration.class);
        verify(mapper, times(7)).insertWorkoutSet(captor.capture());
        assertEquals(List.of("백 스쿼트", "백 스쿼트", "백 스쿼트", "백 스쿼트", "벤치 프레스", "벤치 프레스", "벤치 프레스"),
                captor.getAllValues().stream().map(WorkoutSetRegistration::exerciseName).toList());
    }

    @Test
    void deleteWorkoutDayRemovesSetsBeforeSessions() {
        TrainerPortalService service = new TrainerPortalService(mapper);
        LocalDate date = LocalDate.of(2026, 8, 13);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 30L)).thenReturn(true);
        when(mapper.deleteWorkoutSessionsByDate(10L, 30L, date)).thenReturn(2);

        service.deleteWorkoutDay(10L, 30L, date);

        InOrder order = inOrder(mapper);
        order.verify(mapper).deleteWorkoutSetsByDate(10L, 30L, date);
        order.verify(mapper).deleteWorkoutSessionsByDate(10L, 30L, date);
    }
}
