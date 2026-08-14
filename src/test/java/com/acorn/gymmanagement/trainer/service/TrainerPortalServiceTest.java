package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.trainer.form.TrainerRoutineExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerPortalMapper;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerPortalServiceTest {
    @Mock TrainerPortalMapper mapper;

    @Test
    void createRoutineStoresAllExercisesInOneWorkoutGroup() {
        TrainerPortalService service = new TrainerPortalService(mapper);
        when(mapper.findTrainerId(10L)).thenReturn(Optional.of(20L));
        when(mapper.existsAssignedMember(20L, 30L)).thenReturn(true);
        when(mapper.insertRoutine(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration>getArgument(0).setRoutineId(40L);
            return 1;
        });
        when(mapper.insertWorkoutGroup(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.trainer.model.TrainerRoutineWorkoutGroupRegistration>getArgument(0).setWorkoutGroupId(50L);
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
        assertEquals(List.of(50L, 50L, 50L), captor.getAllValues().stream().map(TrainerRoutineExerciseRegistration::workoutGroupId).toList());
    }
}
