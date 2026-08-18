package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.mypage.dto.response.MemberWorkoutEditView;
import com.acorn.gymmanagement.mypage.form.WorkoutExerciseForm;
import com.acorn.gymmanagement.mypage.form.WorkoutRecordForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPortalMapper;
import com.acorn.gymmanagement.workout.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.workout.model.WorkoutSetRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberPortalServiceWorkoutTest {
    private MemberPortalMapper mapper;
    private MemberPortalService service;

    @BeforeEach
    void setUp() {
        mapper = mock(MemberPortalMapper.class);
        service = new MemberPortalService(mapper);
    }

    @Test
    void savesMultipleExercisesAsOneSessionAndSequentialSets() {
        when(mapper.findActiveMemberId(10L)).thenReturn(Optional.of(20L));
        when(mapper.insertWorkoutSession(any())).thenAnswer(invocation -> {
            WorkoutSessionRegistration session = invocation.getArgument(0);
            session.setSessionId(30L);
            return 1;
        });
        when(mapper.insertWorkoutSet(any())).thenReturn(1);
        var form = new WorkoutRecordForm(40L, 60, " 완료 ", List.of(
                new WorkoutExerciseForm(" 스쿼트 ", 2, new BigDecimal("80.0"), 10),
                new WorkoutExerciseForm("레그 익스텐션", 3, new BigDecimal("35.0"), 12)
        ));

        service.saveWorkout(10L, form);

        var captor = ArgumentCaptor.forClass(WorkoutSetRegistration.class);
        verify(mapper, times(5)).insertWorkoutSet(captor.capture());
        assertThat(captor.getAllValues()).extracting(WorkoutSetRegistration::sessionId).containsOnly(30L);
        assertThat(captor.getAllValues()).extracting(WorkoutSetRegistration::exerciseName)
                .containsExactly("스쿼트", "스쿼트", "레그 익스텐션", "레그 익스텐션", "레그 익스텐션");
        assertThat(captor.getAllValues()).extracting(WorkoutSetRegistration::setNumber)
                .containsExactly(1, 2, 1, 2, 3);
    }

    @Test
    void updateReplacesAllSetsAfterOwnershipCheck() {
        LocalDateTime startedAt = LocalDateTime.of(2026, 8, 13, 13, 20);
        when(mapper.findWorkoutForEdit(10L, 30L)).thenReturn(Optional.of(
                new MemberWorkoutEditView(30L, 40L, startedAt, startedAt.plusMinutes(40), "기존")
        ));
        when(mapper.findActiveMemberId(10L)).thenReturn(Optional.of(20L));
        when(mapper.updateWorkoutSession(any())).thenReturn(1);
        when(mapper.insertWorkoutSet(any())).thenReturn(1);
        var form = new WorkoutRecordForm(40L, 70, "수정", List.of(
                new WorkoutExerciseForm("벤치 프레스", 2, new BigDecimal("60.0"), 8)
        ));

        service.updateWorkout(10L, 30L, form);

        var sessionCaptor = ArgumentCaptor.forClass(WorkoutSessionRegistration.class);
        verify(mapper).updateWorkoutSession(sessionCaptor.capture());
        assertThat(sessionCaptor.getValue().getEndedAt()).isEqualTo(startedAt.plusMinutes(70));
        var order = inOrder(mapper);
        order.verify(mapper).deleteWorkoutSets(30L);
        order.verify(mapper, times(2)).insertWorkoutSet(any());
    }

    @Test
    void deletesSetsBeforeSessionsForTheOwnedDate() {
        LocalDate date = LocalDate.of(2026, 8, 13);
        when(mapper.deleteWorkoutSessionsByDate(10L, date)).thenReturn(2);

        service.deleteWorkoutDay(10L, date);

        var order = inOrder(mapper);
        order.verify(mapper).deleteWorkoutSetsByDate(10L, date);
        order.verify(mapper).deleteWorkoutSessionsByDate(10L, date);
    }
}
