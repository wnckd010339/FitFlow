package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineExerciseView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineView;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineForm;
import com.acorn.gymmanagement.trainer.mapper.TrainerRoutineMapper;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerRoutineService {
    private final TrainerRoutineMapper trainerRoutineMapper;
    private final TrainerAccessValidator accessValidator;

    public List<TrainerRoutineView> routines(Long userId, Long memberId) {
        if (memberId != null) accessValidator.requireAssignedMember(userId, memberId);
        return trainerRoutineMapper.findRoutines(userId, memberId);
    }

    public TrainerRoutineView routine(Long userId, Long routineId) {
        return trainerRoutineMapper.findRoutine(userId, routineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "루틴을 찾을 수 없습니다."));
    }

    public List<TrainerRoutineExerciseView> routineExercises(Long userId, Long routineId) {
        routine(userId, routineId);
        return trainerRoutineMapper.findRoutineExercises(routineId);
    }

    public boolean isRoutineAvailableForMember(Long userId, Long routineId, Long memberId) {
        return trainerRoutineMapper.existsRoutineForMember(userId, routineId, memberId);
    }

    @Transactional
    public void createRoutine(Long userId, TrainerRoutineForm form) {
        validate(form);
        Long trainerId = accessValidator.requireTrainerId(userId);
        accessValidator.requireAssignedMemberByTrainerId(trainerId, form.getMemberId());
        TrainerRoutineRegistration registration = registration(form, trainerId);
        if (trainerRoutineMapper.insertRoutine(registration) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "루틴을 등록하지 못했습니다.");
        }
        saveExercises(registration.getRoutineId(), form);
    }

    @Transactional
    public void updateRoutine(Long userId, Long routineId, TrainerRoutineForm form) {
        validate(form);
        TrainerRoutineView current = routine(userId, routineId);
        accessValidator.requireAssignedMember(userId, form.getMemberId());
        if (!current.memberId().equals(form.getMemberId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "루틴의 회원은 변경할 수 없습니다.");
        }
        TrainerRoutineRegistration registration = registration(form, accessValidator.requireTrainerId(userId));
        registration.setRoutineId(routineId);
        if (trainerRoutineMapper.updateRoutine(registration) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "루틴을 수정하지 못했습니다.");
        }
        trainerRoutineMapper.deleteRoutineExercises(routineId);
        saveExercises(routineId, form);
    }

    @Transactional
    public void deleteRoutine(Long userId, Long routineId) {
        routine(userId, routineId);
        if (trainerRoutineMapper.cancelRoutine(userId, routineId) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "운동 루틴을 삭제하지 못했습니다.");
        }
    }

    private TrainerRoutineRegistration registration(TrainerRoutineForm form, Long trainerId) {
        return new TrainerRoutineRegistration(
                form.getMemberId(), trainerId, form.getTitle().trim(), blankToNull(form.getDescription()),
                form.getStartDate(), form.getEndDate());
    }

    private void saveExercises(Long routineId, TrainerRoutineForm form) {
        for (int index = 0; index < form.getExercises().size(); index++) {
            TrainerRoutineExerciseForm exercise = form.getExercises().get(index);
            int inserted = trainerRoutineMapper.insertRoutineExercise(
                    new TrainerRoutineExerciseRegistration(
                            routineId, exercise.getExerciseName().trim(), index + 1,
                            exercise.getTargetSets(), exercise.getTargetRepsMin(),
                            exercise.getTargetRepsMax(), exercise.getTargetWeight(),
                            exercise.getRestSeconds(), blankToNull(exercise.getMemo())));
            if (inserted != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 항목을 저장하지 못했습니다.");
            }
        }
    }

    private void validate(TrainerRoutineForm form) {
        if (form.getEndDate() != null && form.getEndDate().isBefore(form.getStartDate())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "종료일은 시작일보다 빠를 수 없습니다.");
        }
        for (TrainerRoutineExerciseForm exercise : form.getExercises()) {
            if (exercise.getTargetRepsMin() != null && exercise.getTargetRepsMax() != null
                    && exercise.getTargetRepsMax() < exercise.getTargetRepsMin()) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_ERROR,
                        "최대 반복 횟수는 최소 반복 횟수 이상이어야 합니다.");
            }
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
