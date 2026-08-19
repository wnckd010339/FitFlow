package com.acorn.gymmanagement.mypage.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.dto.response.*;
import com.acorn.gymmanagement.mypage.form.WorkoutExerciseForm;
import com.acorn.gymmanagement.mypage.form.WorkoutRecordForm;
import com.acorn.gymmanagement.mypage.mapper.MemberPortalMapper;
import com.acorn.gymmanagement.workout.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.workout.model.WorkoutSetRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberWorkoutService {
    private final MemberPortalMapper mapper;

    public List<MemberRoutineView> routine(Long userId) { return mapper.findActiveRoutine(userId); }
    public List<MemberWorkoutDayView> workoutDays(Long userId) { return mapper.findWorkoutDays(userId); }
    public List<MemberWorkoutDetailView> workoutsByDate(Long userId, LocalDate date) { return mapper.findWorkoutsByDate(userId, date); }
    public MemberWorkoutEditView workoutForEdit(Long userId, Long sessionId) { return mapper.findWorkoutForEdit(userId, sessionId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "수정할 운동 기록을 찾을 수 없습니다.")); }
    public List<MemberWorkoutExerciseView> workoutExercises(Long userId, Long sessionId) { workoutForEdit(userId, sessionId); return mapper.findWorkoutExercises(userId, sessionId); }

    @Transactional
    public void save(Long userId, WorkoutRecordForm form) {
        validateExercises(form);
        Long memberId = activeMemberId(userId);
        LocalDateTime now = LocalDateTime.now();
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(memberId, form.getRoutineId(), now.minusMinutes(form.getDurationMinutes()), now, blankToNull(form.getMemo()));
        if (mapper.insertWorkoutSession(session) != 1) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 기록을 저장하지 못했습니다.");
        saveExercises(session.getSessionId(), form);
    }

    @Transactional
    public void update(Long userId, Long sessionId, WorkoutRecordForm form) {
        validateExercises(form);
        MemberWorkoutEditView current = workoutForEdit(userId, sessionId);
        WorkoutSessionRegistration session = new WorkoutSessionRegistration(sessionId, activeMemberId(userId), form.getRoutineId(), current.startedAt(), current.startedAt().plusMinutes(form.getDurationMinutes()), blankToNull(form.getMemo()));
        if (mapper.updateWorkoutSession(session) != 1) throw new BusinessException(ErrorCode.CONFLICT, "운동 기록이 변경되어 수정하지 못했습니다.");
        mapper.deleteWorkoutSets(sessionId);
        saveExercises(sessionId, form);
    }

    @Transactional
    public void deleteDay(Long userId, LocalDate date) {
        mapper.deleteWorkoutSetsByDate(userId, date);
        if (mapper.deleteWorkoutSessionsByDate(userId, date) < 1) throw new BusinessException(ErrorCode.NOT_FOUND, "삭제할 운동 기록을 찾을 수 없습니다.");
    }

    @Transactional
    public void delete(Long userId, Long sessionId) {
        workoutForEdit(userId, sessionId);
        mapper.deleteWorkoutSets(sessionId);
        if (mapper.deleteWorkoutSession(userId, sessionId) != 1) throw new BusinessException(ErrorCode.CONFLICT, "운동 기록이 변경되어 삭제하지 못했습니다.");
    }

    private Long activeMemberId(Long userId) { return mapper.findActiveMemberId(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "활성 회원 정보를 찾을 수 없습니다.")); }
    private void saveExercises(Long sessionId, WorkoutRecordForm form) { for (WorkoutExerciseForm exercise : form.getExercises()) for (int number=1; number<=exercise.getSets(); number++) if (mapper.insertWorkoutSet(new WorkoutSetRegistration(sessionId, exercise.getExerciseName().trim(), number, exercise.getWeight(), exercise.getReps())) != 1) throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 항목을 저장하지 못했습니다."); }
    private void validateExercises(WorkoutRecordForm form) { Set<String> names=new HashSet<>(); for (WorkoutExerciseForm exercise:form.getExercises()) if (!names.add(exercise.getExerciseName()==null?"":exercise.getExerciseName().trim().toLowerCase())) throw new BusinessException(ErrorCode.VALIDATION_ERROR,"같은 운동 종목을 중복해서 입력할 수 없습니다."); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
