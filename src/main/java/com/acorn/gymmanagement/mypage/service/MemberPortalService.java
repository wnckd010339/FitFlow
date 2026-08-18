package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.form.WorkoutRecordForm;
import com.acorn.gymmanagement.mypage.form.WorkoutExerciseForm;
import com.acorn.gymmanagement.mypage.form.MemberProfileForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPortalMapper;
import com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
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
    public List<MemberWorkoutDayView> workoutDays(Long userId) { return memberPortalMapper.findWorkoutDays(userId); }
    public List<MemberWorkoutDetailView> workoutsByDate(Long userId, java.time.LocalDate date) { return memberPortalMapper.findWorkoutsByDate(userId,date); }
    public List<MemberPaymentView> payments(Long userId) { return memberPortalMapper.findPayments(userId); }

    public MemberProfileView profile(Long userId) {
        return memberPortalMapper.findProfile(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateProfile(Long userId, MemberProfileForm form) {
        MemberProfileForm normalized = new MemberProfileForm(
                form.name().trim(), normalizePhone(form.phone()), form.birthDate(), form.gender(),
                form.email() == null || form.email().isBlank() ? null : form.email().trim().toLowerCase()
        );
        if (memberPortalMapper.updateMemberProfile(userId, normalized) != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 회원 정보를 찾을 수 없습니다.");
        }
        try {
            if (memberPortalMapper.updateUserEmail(userId, normalized.email()) != 1) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 계정 정보를 찾을 수 없습니다.");
            }
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
    }

    private String normalizePhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 11) return digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        if (digits.length() == 10) return digits.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        return phone.trim();
    }

    @Transactional
    public void saveWorkout(Long userId, WorkoutRecordForm form) {
        Long memberId = memberPortalMapper.findActiveMemberId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "활성 회원 정보를 찾을 수 없습니다."));
        LocalDateTime now = LocalDateTime.now();
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(
                memberId, form.getRoutineId(), now.minusMinutes(form.getDurationMinutes()), now, blankToNull(form.getMemo())
        );
        if (memberPortalMapper.insertWorkoutSession(session) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 기록을 저장하지 못했습니다.");
        }
        saveExercises(session.getSessionId(), form);
    }

    public MemberWorkoutEditView workoutForEdit(Long userId, Long sessionId) {
        return memberPortalMapper.findWorkoutForEdit(userId, sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "수정할 운동 기록을 찾을 수 없습니다."));
    }
    public List<MemberWorkoutExerciseView> workoutExercises(Long userId, Long sessionId) {
        workoutForEdit(userId, sessionId);
        return memberPortalMapper.findWorkoutExercises(userId, sessionId);
    }

    @Transactional
    public void updateWorkout(Long userId, Long sessionId, WorkoutRecordForm form) {
        MemberWorkoutEditView current = workoutForEdit(userId, sessionId);
        Long memberId = memberPortalMapper.findActiveMemberId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "활성 회원 정보를 찾을 수 없습니다."));
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(
                sessionId, memberId, form.getRoutineId(), current.startedAt(),
                current.startedAt().plusMinutes(form.getDurationMinutes()), blankToNull(form.getMemo())
        );
        if (memberPortalMapper.updateWorkoutSession(session) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 기록이 변경되어 수정하지 못했습니다.");
        }
        memberPortalMapper.deleteWorkoutSets(sessionId);
        saveExercises(sessionId, form);
    }

    @Transactional
    public void deleteWorkoutDay(Long userId, java.time.LocalDate date) {
        memberPortalMapper.deleteWorkoutSetsByDate(userId, date);
        if (memberPortalMapper.deleteWorkoutSessionsByDate(userId, date) < 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "삭제할 운동 기록을 찾을 수 없습니다.");
        }
    }

    private void saveExercises(Long sessionId, WorkoutRecordForm form) {
        for (WorkoutExerciseForm exercise : form.getExercises()) {
            for (int number = 1; number <= exercise.getSets(); number++) {
                var workoutSet = new WorkoutSetRegistration(
                        sessionId, exercise.getExerciseName().trim(), number, exercise.getWeight(), exercise.getReps()
                );
                if (memberPortalMapper.insertWorkoutSet(workoutSet) != 1) {
                    throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 항목을 저장하지 못했습니다.");
                }
            }
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
