package com.acorn.gymmanagement.trainer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineView;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerRoutineMapper;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerRoutineServiceTest {
    @Mock TrainerRoutineMapper mapper;
    @Mock TrainerAccessValidator accessValidator;

    @Test
    void createRoutineStoresEveryExerciseInDisplayOrder() {
        TrainerRoutineService service = new TrainerRoutineService(mapper, accessValidator);
        when(accessValidator.requireTrainerId(10L)).thenReturn(20L);
        when(mapper.insertRoutine(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration>
                    getArgument(0).setRoutineId(40L);
            return 1;
        });
        when(mapper.insertRoutineExercise(any())).thenReturn(1);
        TrainerRoutineForm form = routineForm();

        service.createRoutine(10L, form);

        ArgumentCaptor<TrainerRoutineExerciseRegistration> captor =
                ArgumentCaptor.forClass(TrainerRoutineExerciseRegistration.class);
        verify(mapper, times(2)).insertRoutineExercise(captor.capture());
        assertEquals(List.of(1, 2), captor.getAllValues().stream()
                .map(TrainerRoutineExerciseRegistration::displayOrder).toList());
        verify(accessValidator).requireAssignedMemberByTrainerId(20L, 30L);
    }

    @Test
    void updateRoutineReplacesExistingExercisesAfterRoutineUpdate() {
        TrainerRoutineService service = new TrainerRoutineService(mapper, accessValidator);
        TrainerRoutineForm form = routineForm();
        when(mapper.findRoutine(10L, 40L)).thenReturn(Optional.of(new TrainerRoutineView(
                40L, 30L, "회원", "기존 루틴", null,
                LocalDate.of(2026, 8, 1), null, 1)));
        when(accessValidator.requireTrainerId(10L)).thenReturn(20L);
        when(mapper.updateRoutine(any())).thenReturn(1);
        when(mapper.insertRoutineExercise(any())).thenReturn(1);

        service.updateRoutine(10L, 40L, form);

        InOrder order = inOrder(mapper);
        order.verify(mapper).updateRoutine(any());
        order.verify(mapper).deleteRoutineExercises(40L);
        order.verify(mapper, times(2)).insertRoutineExercise(any());
    }

    private TrainerRoutineForm routineForm() {
        return new TrainerRoutineForm(
                30L, "4주 근력 향상", "하체 중심", LocalDate.of(2026, 8, 14),
                LocalDate.of(2026, 9, 10),
                List.of(
                        new TrainerRoutineExerciseForm("백 스쿼트", 4, 8, 10, null, 90, null),
                        new TrainerRoutineExerciseForm("레그 프레스", 4, 10, 12, null, 90, null)));
    }
}
