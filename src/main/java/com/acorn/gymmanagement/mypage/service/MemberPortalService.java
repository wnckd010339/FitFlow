package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.form.WorkoutRecordForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPortalMapper;
import com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPortalService {
    private final MemberPortalMapper memberPortalMapper;

    public List<MemberMembershipView> memberships(Long userId) { return memberPortalMapper.findMemberships(userId); }
    public List<MemberAttendanceView> attendances(Long userId) { return memberPortalMapper.findAttendances(userId); }
    public List<MemberRoutineView> routine(Long userId) { return memberPortalMapper.findActiveRoutine(userId); }
    public List<MemberWorkoutView> workouts(Long userId) { return memberPortalMapper.findWorkouts(userId); }
    public List<MemberPaymentView> payments(Long userId) { return memberPortalMapper.findPayments(userId); }

    @Transactional
    public void saveWorkout(Long userId, WorkoutRecordForm form) {
        Long memberId = memberPortalMapper.findActiveMemberId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "활성 회원 정보를 찾을 수 없습니다."));
        LocalDateTime now = LocalDateTime.now();
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(
                memberId, form.routineId(), now.minusMinutes(form.durationMinutes()), now, form.memo()
        );
        if (memberPortalMapper.insertWorkoutSession(session) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 기록을 저장하지 못했습니다.");
        }
        for (int number = 1; number <= form.sets(); number++) {
            memberPortalMapper.insertWorkoutSet(new WorkoutSetRegistration(session.getSessionId(), form.exerciseName(), number, form.weight(), form.reps()));
        }
    }

    public MemberWorkoutEditView workoutForEdit(Long userId, Long sessionId) {
        return memberPortalMapper.findWorkoutForEdit(userId, sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "수정할 운동 기록을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateWorkout(Long userId, Long sessionId, WorkoutRecordForm form) {
        MemberWorkoutEditView current = workoutForEdit(userId, sessionId);
        Long memberId = memberPortalMapper.findActiveMemberId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "활성 회원 정보를 찾을 수 없습니다."));
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(
                sessionId, memberId, form.routineId(), current.startedAt(),
                current.startedAt().plusMinutes(form.durationMinutes()), form.memo()
        );
        if (memberPortalMapper.updateWorkoutSession(session) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 기록이 변경되어 수정하지 못했습니다.");
        }
        memberPortalMapper.deleteWorkoutSets(sessionId);
        for (int number = 1; number <= form.sets(); number++) {
            memberPortalMapper.insertWorkoutSet(new WorkoutSetRegistration(sessionId, form.exerciseName(), number, form.weight(), form.reps()));
        }
    }
}
