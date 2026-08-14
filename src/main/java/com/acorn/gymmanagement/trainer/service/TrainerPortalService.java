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
    public List<TrainerRoutineView> routines(Long userId, Long memberId) { if (memberId != null) assertAssignedMember(userId, memberId); return mapper.findRoutines(userId, memberId); }
    public TrainerRoutineView routine(Long userId, Long routineId) { return mapper.findRoutine(userId, routineId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "루틴을 찾을 수 없습니다.")); }
    public TrainerWorkoutView workout(Long userId, Long sessionId) { return mapper.findWorkout(userId, sessionId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "운동 기록을 찾을 수 없습니다.")); }
    @Transactional public void updateProfile(Long userId, TrainerProfileForm form) { if (mapper.updateProfile(userId, new TrainerProfileForm(form.name().trim(), normalizePhone(form.phone()), blankToNull(form.specialty()))) != 1) throw new BusinessException(ErrorCode.NOT_FOUND, "수정할 트레이너 정보를 찾을 수 없습니다."); }
    @Transactional public void createRoutine(Long userId, TrainerRoutineForm form) { Long trainerId=trainerId(userId); assertAssignedMemberByTrainerId(trainerId, form.memberId()); TrainerRoutineRegistration r=new TrainerRoutineRegistration(form.memberId(),trainerId,form.title().trim(),blankToNull(form.description()),form.startDate(),form.endDate()); mapper.insertRoutine(r); saveExercise(r.getRoutineId(),form); }
    @Transactional public void updateRoutine(Long userId, Long routineId, TrainerRoutineForm form) { TrainerRoutineView current=routine(userId,routineId); assertAssignedMember(userId,form.memberId()); if (!current.memberId().equals(form.memberId())) throw new BusinessException(ErrorCode.CONFLICT,"루틴의 회원은 변경할 수 없습니다."); Long trainerId=trainerId(userId); TrainerRoutineRegistration r=new TrainerRoutineRegistration(form.memberId(),trainerId,form.title().trim(),blankToNull(form.description()),form.startDate(),form.endDate()); r.setRoutineId(routineId); if(mapper.updateRoutine(r)!=1) throw new BusinessException(ErrorCode.CONFLICT,"루틴을 수정하지 못했습니다."); mapper.deleteRoutineExercises(routineId); saveExercise(routineId,form); }
    @Transactional public void createWorkout(Long userId, TrainerWorkoutForm form) { assertAssignedMember(userId,form.memberId()); LocalDateTime now=LocalDateTime.now(); WorkoutSessionRegistration s=new WorkoutSessionRegistration(form.memberId(),form.routineId(),now.minusMinutes(form.durationMinutes()),now,blankToNull(form.memo())); mapper.insertWorkoutSession(s); saveSets(s.getSessionId(),form); }
    @Transactional public void updateWorkout(Long userId, Long sessionId, TrainerWorkoutForm form) { TrainerWorkoutView current=workout(userId,sessionId); assertAssignedMember(userId,form.memberId()); if(!current.memberId().equals(form.memberId())) throw new BusinessException(ErrorCode.CONFLICT,"운동 기록의 회원은 변경할 수 없습니다."); LocalDateTime started=current.startedAt(); WorkoutSessionRegistration s=new WorkoutSessionRegistration(sessionId,form.memberId(),form.routineId(),started,started.plusMinutes(form.durationMinutes()),blankToNull(form.memo())); if(mapper.updateWorkoutSession(s)!=1) throw new BusinessException(ErrorCode.CONFLICT,"운동 기록을 수정하지 못했습니다."); mapper.deleteWorkoutSets(sessionId); saveSets(sessionId,form); }
    private void saveExercise(Long routineId, TrainerRoutineForm f) { mapper.insertRoutineExercise(new TrainerRoutineExerciseRegistration(routineId,f.exerciseName().trim(),f.targetSets(),f.targetRepsMin(),f.targetRepsMax(),f.targetWeight(),f.restSeconds(),f.dayOfWeek())); }
    private void saveSets(Long sessionId, TrainerWorkoutForm f) { for(int n=1;n<=f.sets();n++) mapper.insertWorkoutSet(new WorkoutSetRegistration(sessionId,f.exerciseName().trim(),n,f.weight(),f.reps())); }
    private Long trainerId(Long userId) { return mapper.findTrainerId(userId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,"트레이너 정보를 찾을 수 없습니다.")); }
    private void assertAssignedMember(Long userId, Long memberId) { assertAssignedMemberByTrainerId(trainerId(userId),memberId); }
    private void assertAssignedMemberByTrainerId(Long trainerId, Long memberId) { if(!mapper.existsAssignedMember(trainerId,memberId)) throw new BusinessException(ErrorCode.FORBIDDEN,"담당 회원만 조회하거나 수정할 수 있습니다."); }
    private String normalizePhone(String value) { String d=value.replaceAll("[^0-9]",""); return d.length()==11?d.replaceFirst("(\\d{3})(\\d{4})(\\d{4})","$1-$2-$3"):value.trim(); }
    private String blankToNull(String value) { return value==null||value.isBlank()?null:value.trim(); }
}
