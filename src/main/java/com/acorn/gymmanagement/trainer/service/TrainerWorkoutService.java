package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutDayView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutExerciseView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerRoutineMapper;
import com.acorn.gymmanagement.trainer.mapper.TrainerWorkoutMapper;
import com.acorn.gymmanagement.workout.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.workout.model.WorkoutSetRegistration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerWorkoutService {
    private final TrainerWorkoutMapper trainerWorkoutMapper;
    private final TrainerRoutineMapper trainerRoutineMapper;
    private final TrainerAccessValidator accessValidator;

    public List<TrainerWorkoutDayView> workoutDays(Long userId, Long memberId) {
        if (memberId != null) accessValidator.requireAssignedMember(userId, memberId);
        return trainerWorkoutMapper.findWorkoutDays(userId, memberId);
    }

    public List<TrainerWorkoutView> workoutsByDate(
            Long userId, Long memberId, LocalDate workoutDate) {
        accessValidator.requireAssignedMember(userId, memberId);
        return trainerWorkoutMapper.findWorkoutsByDate(userId, memberId, workoutDate);
    }

    public TrainerWorkoutView workout(Long userId, Long sessionId) {
        return trainerWorkoutMapper.findWorkout(userId, sessionId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "운동 기록을 찾을 수 없습니다."));
    }

    public List<TrainerWorkoutExerciseView> workoutExercises(Long userId, Long sessionId) {
        workout(userId, sessionId);
        return trainerWorkoutMapper.findWorkoutExercises(userId, sessionId);
    }

    @Transactional
    public void createWorkout(Long userId, TrainerWorkoutForm form) {
        validate(userId, form);
        LocalDateTime endedAt = LocalDateTime.now();
        var registration = new WorkoutSessionRegistration(
                form.getMemberId(), form.getRoutineId(),
                endedAt.minusMinutes(form.getDurationMinutes()), endedAt,
                blankToNull(form.getMemo()));
        if (trainerWorkoutMapper.insertWorkoutSession(registration) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 기록을 저장하지 못했습니다.");
        }
        saveSets(registration.getSessionId(), form);
    }

    @Transactional
    public void updateWorkout(Long userId, Long sessionId, TrainerWorkoutForm form) {
        TrainerWorkoutView current = workout(userId, sessionId);
        validate(userId, form);
        if (!current.memberId().equals(form.getMemberId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 기록의 회원은 변경할 수 없습니다.");
        }
        LocalDateTime startedAt = current.startedAt();
        var registration = new WorkoutSessionRegistration(
                sessionId, form.getMemberId(), form.getRoutineId(), startedAt,
                startedAt.plusMinutes(form.getDurationMinutes()), blankToNull(form.getMemo()));
        if (trainerWorkoutMapper.updateWorkoutSession(registration) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 기록을 수정하지 못했습니다.");
        }
        trainerWorkoutMapper.deleteWorkoutSets(sessionId);
        saveSets(sessionId, form);
    }

    @Transactional
    public void deleteWorkout(Long userId, Long sessionId) {
        workout(userId, sessionId);
        trainerWorkoutMapper.deleteWorkoutSets(sessionId);
        if (trainerWorkoutMapper.deleteWorkoutSession(userId, sessionId) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 기록을 삭제하지 못했습니다.");
        }
    }

    @Transactional
    public void deleteWorkoutDay(Long userId, Long memberId, LocalDate workoutDate) {
        accessValidator.requireAssignedMember(userId, memberId);
        trainerWorkoutMapper.deleteWorkoutSetsByDate(userId, memberId, workoutDate);
        if (trainerWorkoutMapper.deleteWorkoutSessionsByDate(userId, memberId, workoutDate) < 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "삭제할 운동 기록을 찾을 수 없습니다.");
        }
    }

    private void validate(Long userId, TrainerWorkoutForm form) {
        accessValidator.requireAssignedMember(userId, form.getMemberId());
        if (form.getDurationMinutes() == null
                || form.getDurationMinutes() < 1 || form.getDurationMinutes() > 1440) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "운동 시간은 1~1440분이어야 합니다.");
        }
        if (form.getExercises() == null || form.getExercises().isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "운동 항목을 하나 이상 입력해 주세요.");
        }
        if (form.getRoutineId() != null
                && !trainerRoutineMapper.existsRoutineForMember(
                        userId, form.getRoutineId(), form.getMemberId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR, "선택한 회원에게 등록된 루틴이 아닙니다.");
        }
        for (TrainerWorkoutExerciseForm exercise : form.getExercises()) {
            if (exercise.getExerciseName() == null || exercise.getExerciseName().isBlank()
                    || exercise.getSets() == null || exercise.getSets() < 1 || exercise.getSets() > 20) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "운동명과 세트 수를 확인해 주세요.");
            }
        }
    }

    private void saveSets(Long sessionId, TrainerWorkoutForm form) {
        for (TrainerWorkoutExerciseForm exercise : form.getExercises()) {
            for (int setNumber = 1; setNumber <= exercise.getSets(); setNumber++) {
                int inserted = trainerWorkoutMapper.insertWorkoutSet(new WorkoutSetRegistration(
                        sessionId, exercise.getExerciseName().trim(), setNumber,
                        exercise.getWeight(), exercise.getReps()));
                if (inserted != 1) {
                    throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 세트를 저장하지 못했습니다.");
                }
            }
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
