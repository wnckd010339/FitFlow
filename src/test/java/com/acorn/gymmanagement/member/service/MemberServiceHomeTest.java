package com.acorn.gymmanagement.member.service;

import com.acorn.gymmanagement.member.dto.response.MemberHomeRoutineResponse;
import com.acorn.gymmanagement.member.dto.response.MemberHomeSummaryResponse;
import com.acorn.gymmanagement.member.mapper.MemberMapper;
import com.acorn.gymmanagement.member.view.MemberHomeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceHomeTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberMapper, passwordEncoder);
    }

    @Test
    void homeLoadsDataForTheLoggedInUser() {
        MemberHomeSummaryResponse summary = summary();
        MemberHomeRoutineResponse routine = new MemberHomeRoutineResponse(
                7L, "전신 운동", "기초 체력 루틴"
        );

        when(memberMapper.findHomeSummaryByUserId(3L)).thenReturn(Optional.of(summary));
        when(memberMapper.findHomeRoutineByUserId(3L)).thenReturn(Optional.of(routine));
        when(memberMapper.findTodayRoutineExercises(7L)).thenReturn(List.of());
        when(memberMapper.findOpenAttendanceByUserId(3L)).thenReturn(Optional.empty());
        when(memberMapper.findHomeTrainerByUserId(3L)).thenReturn(Optional.empty());
        when(memberMapper.findRecentHomeWorkoutsByUserId(3L)).thenReturn(List.of());

        MemberHomeView result = memberService.findHomeView(3L);

        assertEquals("테스트 회원", result.summary().memberName());
        assertEquals(7L, result.routine().routineId());
        assertEquals(4, result.weeklyWorkoutGoal());
    }

    @Test
    void homeWithoutRoutineDoesNotQueryRoutineExercises() {
        when(memberMapper.findHomeSummaryByUserId(3L)).thenReturn(Optional.of(summary()));
        when(memberMapper.findHomeRoutineByUserId(3L)).thenReturn(Optional.empty());
        when(memberMapper.findOpenAttendanceByUserId(3L)).thenReturn(Optional.empty());
        when(memberMapper.findHomeTrainerByUserId(3L)).thenReturn(Optional.empty());
        when(memberMapper.findRecentHomeWorkoutsByUserId(3L)).thenReturn(List.of());

        MemberHomeView result = memberService.findHomeView(3L);

        assertNull(result.routine());
        assertEquals(List.of(), result.exercises());
        verify(memberMapper, never()).findTodayRoutineExercises(7L);
    }

    private MemberHomeSummaryResponse summary() {
        return new MemberHomeSummaryResponse(
                1L, "테스트 회원", 1, 2, 1,
                null, null, null, 0
        );
    }
}
