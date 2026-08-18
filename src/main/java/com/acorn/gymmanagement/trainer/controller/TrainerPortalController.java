package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineView;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.trainer.form.*;
import com.acorn.gymmanagement.trainer.service.TrainerPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequestMapping("/trainer") @RequiredArgsConstructor
public class TrainerPortalController {
 private final TrainerPortalService service;
 @ModelAttribute("headerProfile")
 public com.acorn.gymmanagement.trainer.dto.response.TrainerProfileView headerProfile(
         @SessionAttribute(SessionUser.SESSION_KEY) SessionUser user) {
     return service.profile(user.userId());
 }
 @GetMapping("/members") String members(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam(required=false) String keyword,Model m){m.addAttribute("members",service.members(u.userId(),keyword));m.addAttribute("keyword",keyword);return "trainer/members";}
 @GetMapping("/members/{memberId}") String member(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long memberId,Model m){m.addAttribute("member",service.member(u.userId(),memberId));m.addAttribute("attendances",service.attendances(u.userId(),memberId));m.addAttribute("workoutDays",service.workoutDays(u.userId(),memberId));m.addAttribute("routines",service.routines(u.userId(),memberId));return "trainer/member-detail";}
 @GetMapping("/routines") String routines(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam(required=false) Long memberId,Model m){m.addAttribute("members",service.members(u.userId(),null));m.addAttribute("routines",service.routines(u.userId(),memberId));m.addAttribute("selectedMemberId",memberId);return "trainer/routines";}
 @GetMapping("/routines/new") String newRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){
     TrainerRoutineExerciseForm exercise = new TrainerRoutineExerciseForm("",3,8,12,null,60,"");
     routineForm(m,new TrainerRoutineForm(null,"","",java.time.LocalDate.now(),null,"",1,null,java.util.List.of(exercise)),u.userId());
     return "trainer/routine-form";
 }
 @PostMapping("/routines") String createRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute TrainerRoutineForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){routineForm(m,form,u.userId());return "trainer/routine-form";}service.createRoutine(u.userId(),form);r.addFlashAttribute("message","운동 루틴을 등록했습니다.");return "redirect:/trainer/routines";}
 @GetMapping("/routines/{routineId}/edit") String editRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long routineId,Model m){
     TrainerRoutineView v=service.routine(u.userId(),routineId);
     java.util.List<TrainerRoutineExerciseForm> exercises=service.routineExercises(u.userId(),routineId).stream()
             .map(e->new TrainerRoutineExerciseForm(e.exerciseName(),e.targetSets(),e.targetRepsMin(),
                     e.targetRepsMax(),e.targetWeight(),e.restSeconds(),e.memo())).toList();
     if(exercises.isEmpty()) exercises=java.util.List.of(new TrainerRoutineExerciseForm("",3,8,12,null,60,""));
     routineForm(m,new TrainerRoutineForm(v.memberId(),v.title(),v.description(),v.startDate(),v.endDate(),
             v.workoutGroupTitle(),v.weekNumber(),v.dayOfWeek(),exercises),u.userId());
     m.addAttribute("routineId",routineId);return "trainer/routine-form";
 }
 @PostMapping("/routines/{routineId}") String updateRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long routineId,@Valid @ModelAttribute TrainerRoutineForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){routineForm(m,form,u.userId());m.addAttribute("routineId",routineId);return "trainer/routine-form";}service.updateRoutine(u.userId(),routineId,form);r.addFlashAttribute("message","운동 루틴을 수정했습니다.");return "redirect:/trainer/routines";}
 @PostMapping("/routines/{routineId}/delete") String deleteRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long routineId,RedirectAttributes r){service.deleteRoutine(u.userId(),routineId);r.addFlashAttribute("message","운동 루틴을 삭제했습니다.");return "redirect:/trainer/routines";}
 @GetMapping("/workouts") String workouts(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam(required=false) Long memberId,Model m){m.addAttribute("members",service.members(u.userId(),null));m.addAttribute("workoutDays",service.workoutDays(u.userId(),memberId));m.addAttribute("selectedMemberId",memberId);return "trainer/workouts";}
 @GetMapping("/workouts/daily") String dailyWorkouts(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam Long memberId,@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,Model m){var workouts=service.workoutsByDate(u.userId(),memberId,date);if(workouts.isEmpty())throw new com.acorn.gymmanagement.common.exception.BusinessException(com.acorn.gymmanagement.common.exception.ErrorCode.NOT_FOUND,"해당 날짜의 운동 기록을 찾을 수 없습니다.");m.addAttribute("memberName",workouts.get(0).memberName());m.addAttribute("workoutDate",date);m.addAttribute("workouts",workouts);m.addAttribute("exerciseCount",workouts.size());m.addAttribute("totalSets",workouts.stream().mapToInt(w->w.sets()==null?0:w.sets()).sum());return "trainer/workout-daily-detail";}
 @GetMapping("/workouts/new") String newWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){workoutForm(m,new TrainerWorkoutForm(null,null,60,"",java.util.List.of(new TrainerWorkoutExerciseForm("",3,null,null))),u.userId());return "trainer/workout-form";}
 @PostMapping("/workouts") String createWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute TrainerWorkoutForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){workoutForm(m,form,u.userId());return "trainer/workout-form";}service.createWorkout(u.userId(),form);r.addFlashAttribute("message","운동 기록을 저장했습니다.");return "redirect:/trainer/workouts";}
 @GetMapping("/workouts/{sessionId}/edit") String editWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long sessionId,Model m){TrainerWorkoutView v=service.workout(u.userId(),sessionId);var exercises=service.workoutExercises(u.userId(),sessionId).stream().map(e->new TrainerWorkoutExerciseForm(e.exerciseName(),e.sets(),e.weight(),e.reps())).toList();workoutForm(m,toWorkoutForm(v,exercises),u.userId());m.addAttribute("sessionId",sessionId);return "trainer/workout-form";}
 @PostMapping("/workouts/{sessionId}") String updateWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long sessionId,@Valid @ModelAttribute TrainerWorkoutForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){workoutForm(m,form,u.userId());m.addAttribute("sessionId",sessionId);return "trainer/workout-form";}service.updateWorkout(u.userId(),sessionId,form);r.addFlashAttribute("message","운동 기록을 수정했습니다.");return "redirect:/trainer/workouts";}
 @PostMapping("/workouts/{sessionId}/delete") String deleteWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long sessionId,RedirectAttributes r){service.deleteWorkout(u.userId(),sessionId);r.addFlashAttribute("message","운동 기록을 삭제했습니다.");return "redirect:/trainer/workouts";}
 @PostMapping("/workouts/daily/delete") String deleteWorkoutDay(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam Long memberId,@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,RedirectAttributes r){service.deleteWorkoutDay(u.userId(),memberId,date);r.addFlashAttribute("message","해당 날짜의 운동 기록을 모두 삭제했습니다.");return "redirect:/trainer/workouts";}
 @GetMapping("/profile") String profile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){var p=service.profile(u.userId());m.addAttribute("profileForm",new TrainerProfileForm(p.name(),p.phone(),p.specialty()));return "trainer/profile";}
 @PostMapping("/profile") String updateProfile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute("profileForm") TrainerProfileForm form,BindingResult b,RedirectAttributes r){if(b.hasErrors())return "trainer/profile";service.updateProfile(u.userId(),form);r.addFlashAttribute("message","내 정보를 수정했습니다.");return "redirect:/trainer/profile";}
 private void routineForm(Model m,TrainerRoutineForm f,Long id){m.addAttribute("routineForm",f);m.addAttribute("members",service.members(id,null));}
 private void workoutForm(Model m,TrainerWorkoutForm f,Long id){m.addAttribute("workoutForm",f);m.addAttribute("members",service.members(id,null));}
 static TrainerWorkoutForm toWorkoutForm(TrainerWorkoutView workout, java.util.List<TrainerWorkoutExerciseForm> exercises) {
     long minutes = java.time.Duration.between(workout.startedAt(), workout.endedAt()).toMinutes();
     return new TrainerWorkoutForm(
             workout.memberId(),
             workout.routineId(),
             (int) Math.max(1, minutes),
             workout.memo(),
             exercises
     );
 }
}
