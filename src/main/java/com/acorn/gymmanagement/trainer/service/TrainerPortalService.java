package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.mypage.model.WorkoutSessionRegistration;
import com.acorn.gymmanagement.mypage.model.WorkoutSetRegistration;
import com.acorn.gymmanagement.trainer.dto.response.*;
import com.acorn.gymmanagement.trainer.form.*;
import com.acorn.gymmanagement.trainer.mapper.TrainerPortalMapper;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineExerciseRegistration;
import com.acorn.gymmanagement.trainer.model.TrainerRoutineRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class TrainerPortalService {
    private final TrainerPortalMapper mapper;
    public TrainerProfileView profile(Long userId) { return mapper.findProfile(userId); }
    public List<TrainerMemberView> members(Long userId, String keyword) { return mapper.findMembers(userId, keyword); }
    public TrainerMemberDetailView member(Long userId, Long memberId) { assertAssignedMember(userId, memberId); return mapper.findMemberDetail(userId, memberId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")); }
    public List<TrainerAttendanceView> attendances(Long userId, Long memberId) { assertAssignedMember(userId, memberId); return mapper.findAttendances(memberId); }
    public List<TrainerWorkoutView> workouts(Long userId, Long memberId) { if (memberId != null) assertAssignedMember(userId, memberId); return mapper.findWorkouts(userId, memberId); }
    public List<TrainerWorkoutDayView> workoutDays(Long userId, Long memberId) { if (memberId != null) assertAssignedMember(userId, memberId); return mapper.findWorkoutDays(userId, memberId); }
    public List<TrainerWorkoutView> workoutsByDate(Long userId, Long memberId, java.time.LocalDate workoutDate) { assertAssignedMember(userId, memberId); return mapper.findWorkoutsByDate(userId, memberId, workoutDate); }
    public List<TrainerRoutineView> routines(Long userId, Long memberId) { if (memberId != null) assertAssignedMember(userId, memberId); return mapper.findRoutines(userId, memberId); }
    public TrainerRoutineView routine(Long userId, Long routineId) { return mapper.findRoutine(userId, routineId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "루틴을 찾을 수 없습니다.")); }
    public List<TrainerRoutineExerciseView> routineExercises(Long userId, Long routineId) {
        TrainerRoutineView routine = routine(userId, routineId);
        return mapper.findRoutineExercises(routineId, routine.workoutGroupId());
    }
    public TrainerWorkoutView workout(Long userId, Long sessionId) { return mapper.findWorkout(userId, sessionId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "운동 기록을 찾을 수 없습니다.")); }
    public List<TrainerWorkoutExerciseView> workoutExercises(Long userId, Long sessionId) { workout(userId, sessionId); return mapper.findWorkoutExercises(userId, sessionId); }
    @Transactional public void updateProfile(Long userId, TrainerProfileForm form) { if (mapper.updateProfile(userId, new TrainerProfileForm(form.name().trim(), normalizePhone(form.phone()), blankToNull(form.specialty()))) != 1) throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 트레이너 정보를 찾을 수 없습니다."); }
    @Transactional public void deleteRoutine(Long userId, Long routineId) {
        routine(userId, routineId);
        if (mapper.cancelRoutine(userId, routineId) != 1) throw new BusinessException(ErrorCode.CONFLICT, "운동 루틴을 삭제하지 못했습니다.");
    }
    @Transactional public void deleteWorkout(Long userId, Long sessionId) {
        workout(userId, sessionId);
        mapper.deleteWorkoutSets(sessionId);
        if (mapper.deleteWorkoutSession(userId, sessionId) != 1) throw new BusinessException(ErrorCode.CONFLICT, "운동 기록을 삭제하지 못했습니다.");
    }
    @Transactional public void deleteWorkoutDay(Long userId, Long memberId, java.time.LocalDate workoutDate) {
        assertAssignedMember(userId, memberId);
        mapper.deleteWorkoutSetsByDate(userId, memberId, workoutDate);
        if (mapper.deleteWorkoutSessionsByDate(userId, memberId, workoutDate) < 1) throw new BusinessException(ErrorCode.NOT_FOUND, "삭제할 운동 기록을 찾을 수 없습니다.");
    }
    @Transactional
    public void createRoutine(Long userId, TrainerRoutineForm form) {
        validateRoutineForm(form);
        Long trainerId = trainerId(userId);
        assertAssignedMemberByTrainerId(trainerId, form.getMemberId());
        TrainerRoutineRegistration routine = new TrainerRoutineRegistration(
                form.getMemberId(), trainerId, form.getTitle().trim(), blankToNull(form.getDescription()),
                form.getStartDate(), form.getEndDate());
        if (mapper.insertRoutine(routine) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "루틴을 등록하지 못했습니다.");
        }
        saveExercises(routine.getRoutineId(), null, form);
    }

    @Transactional
    public void updateRoutine(Long userId, Long routineId, TrainerRoutineForm form) {
        validateRoutineForm(form);
        TrainerRoutineView current = routine(userId, routineId);
        assertAssignedMember(userId, form.getMemberId());
        if (!current.memberId().equals(form.getMemberId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "루틴의 회원은 변경할 수 없습니다.");
        }
        Long trainerId = trainerId(userId);
        TrainerRoutineRegistration routine = new TrainerRoutineRegistration(
                form.getMemberId(), trainerId, form.getTitle().trim(), blankToNull(form.getDescription()),
                form.getStartDate(), form.getEndDate());
        routine.setRoutineId(routineId);
        if (mapper.updateRoutine(routine) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "루틴을 수정하지 못했습니다.");
        }
        mapper.deleteRoutineExercises(routineId);
        saveExercises(routineId, null, form);
    }
    @Transactional public void createWorkout(Long userId, TrainerWorkoutForm form) { assertAssignedMember(userId,form.getMemberId()); LocalDateTime now=LocalDateTime.now(); WorkoutSessionRegistration s=new WorkoutSessionRegistration(form.getMemberId(),form.getRoutineId(),now.minusMinutes(form.getDurationMinutes()),now,blankToNull(form.getMemo())); if(mapper.insertWorkoutSession(s)!=1) throw new BusinessException(ErrorCode.INTERNAL_ERROR,"운동 기록을 저장하지 못했습니다."); saveSets(s.getSessionId(),form); }
    @Transactional public void updateWorkout(Long userId, Long sessionId, TrainerWorkoutForm form) { TrainerWorkoutView current=workout(userId,sessionId); assertAssignedMember(userId,form.getMemberId()); if(!current.memberId().equals(form.getMemberId())) throw new BusinessException(ErrorCode.CONFLICT,"운동 기록의 회원은 변경할 수 없습니다."); LocalDateTime started=current.startedAt(); WorkoutSessionRegistration s=new WorkoutSessionRegistration(sessionId,form.getMemberId(),form.getRoutineId(),started,started.plusMinutes(form.getDurationMinutes()),blankToNull(form.getMemo())); if(mapper.updateWorkoutSession(s)!=1) throw new BusinessException(ErrorCode.CONFLICT,"운동 기록을 수정하지 못했습니다."); mapper.deleteWorkoutSets(sessionId); saveSets(sessionId,form); }
    private void saveExercises(Long routineId, Long workoutGroupId, TrainerRoutineForm form) {
        for (int index = 0; index < form.getExercises().size(); index++) {
            TrainerRoutineExerciseForm exercise = form.getExercises().get(index);
            int inserted = mapper.insertRoutineExercise(new TrainerRoutineExerciseRegistration(
                    routineId, workoutGroupId, exercise.getExerciseName().trim(), index + 1,
                    exercise.getTargetSets(), exercise.getTargetRepsMin(), exercise.getTargetRepsMax(),
                    exercise.getTargetWeight(), exercise.getRestSeconds(), blankToNull(exercise.getMemo())));
            if (inserted != 1) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "운동 항목을 저장하지 못했습니다.");
            }
        }
    }
    private void validateRoutineForm(TrainerRoutineForm form) {
        if (form.getEndDate() != null && form.getEndDate().isBefore(form.getStartDate())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "종료일은 시작일보다 빠를 수 없습니다.");
        }
        for (TrainerRoutineExerciseForm exercise : form.getExercises()) {
            if (exercise.getTargetRepsMin() != null && exercise.getTargetRepsMax() != null
                    && exercise.getTargetRepsMax() < exercise.getTargetRepsMin()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "최대 반복 횟수는 최소 반복 횟수 이상이어야 합니다.");
            }
        }
    }
    private void saveSets(Long sessionId, TrainerWorkoutForm form) {
        for (TrainerWorkoutExerciseForm exercise : form.getExercises()) {
            for (int setNumber = 1; setNumber <= exercise.getSets(); setNumber++) {
                int inserted = mapper.insertWorkoutSet(new WorkoutSetRegistration(
                        sessionId, exercise.getExerciseName().trim(), setNumber,
                        exercise.getWeight(), exercise.getReps()));
                if (inserted != 1) throw new BusinessException(ErrorCode.INTERNAL_ERROR,"운동 세트를 저장하지 못했습니다.");
            }
        }
    }
    private Long trainerId(Long userId) { return mapper.findTrainerId(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,"트레이너 정보를 찾을 수 없습니다.")); }
    private void assertAssignedMember(Long userId, Long memberId) { assertAssignedMemberByTrainerId(trainerId(userId),memberId); }
    private void assertAssignedMemberByTrainerId(Long trainerId, Long memberId) { if(!mapper.existsAssignedMember(trainerId,memberId)) throw new BusinessException(ErrorCode.FORBIDDEN,"담당 회원만 조회하거나 수정할 수 있습니다."); }
    private String normalizePhone(String value) { String d=value.replaceAll("[^0-9]",""); return d.length()==11?d.replaceFirst("(\\d{3})(\\d{4})(\\d{4})","$1-$2-$3"):value.trim(); }
    private String blankToNull(String value) { return value==null||value.isBlank()?null:value.trim(); }
}
