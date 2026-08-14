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
 @GetMapping("/members/{memberId}") String member(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long memberId,Model m){m.addAttribute("member",service.member(u.userId(),memberId));m.addAttribute("attendances",service.attendances(u.userId(),memberId));m.addAttribute("workouts",service.workouts(u.userId(),memberId));m.addAttribute("routines",service.routines(u.userId(),memberId));return "trainer/member-detail";}
 @GetMapping("/routines") String routines(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam(required=false) Long memberId,Model m){m.addAttribute("members",service.members(u.userId(),null));m.addAttribute("routines",service.routines(u.userId(),memberId));m.addAttribute("selectedMemberId",memberId);return "trainer/routines";}
 @GetMapping("/routines/new") String newRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){routineForm(m,new TrainerRoutineForm(null,"","",java.time.LocalDate.now(),null,"",3,8,12,null,60,null),u.userId());return "trainer/routine-form";}
 @PostMapping("/routines") String createRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute TrainerRoutineForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){routineForm(m,form,u.userId());return "trainer/routine-form";}service.createRoutine(u.userId(),form);r.addFlashAttribute("message","운동 루틴을 등록했습니다.");return "redirect:/trainer/routines";}
 @GetMapping("/routines/{routineId}/edit") String editRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long routineId,Model m){TrainerRoutineView v=service.routine(u.userId(),routineId);routineForm(m,new TrainerRoutineForm(v.memberId(),v.title(),v.description(),v.startDate(),v.endDate(),v.exerciseName(),v.targetSets(),v.targetRepsMin(),v.targetRepsMax(),v.targetWeight(),v.restSeconds(),v.dayOfWeek()),u.userId());m.addAttribute("routineId",routineId);return "trainer/routine-form";}
 @PostMapping("/routines/{routineId}") String updateRoutine(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long routineId,@Valid @ModelAttribute TrainerRoutineForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){routineForm(m,form,u.userId());m.addAttribute("routineId",routineId);return "trainer/routine-form";}service.updateRoutine(u.userId(),routineId,form);r.addFlashAttribute("message","운동 루틴을 수정했습니다.");return "redirect:/trainer/routines";}
 @GetMapping("/workouts") String workouts(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@RequestParam(required=false) Long memberId,Model m){m.addAttribute("members",service.members(u.userId(),null));m.addAttribute("workouts",service.workouts(u.userId(),memberId));m.addAttribute("selectedMemberId",memberId);return "trainer/workouts";}
 @GetMapping("/workouts/new") String newWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){workoutForm(m,new TrainerWorkoutForm(null,null,"",3,null,null,60,""),u.userId());return "trainer/workout-form";}
 @PostMapping("/workouts") String createWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute TrainerWorkoutForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){workoutForm(m,form,u.userId());return "trainer/workout-form";}service.createWorkout(u.userId(),form);r.addFlashAttribute("message","운동 기록을 저장했습니다.");return "redirect:/trainer/workouts";}
 @GetMapping("/workouts/{sessionId}/edit") String editWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long sessionId,Model m){TrainerWorkoutView v=service.workout(u.userId(),sessionId);long minutes=java.time.Duration.between(v.startedAt(),v.endedAt()).toMinutes();workoutForm(m,new TrainerWorkoutForm(v.memberId(),null,v.exerciseName(),v.sets(),null,null,(int)Math.max(1,minutes),v.memo()),u.userId());m.addAttribute("sessionId",sessionId);return "trainer/workout-form";}
 @PostMapping("/workouts/{sessionId}") String updateWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@PathVariable Long sessionId,@Valid @ModelAttribute TrainerWorkoutForm form,BindingResult b,Model m,RedirectAttributes r){if(b.hasErrors()){workoutForm(m,form,u.userId());m.addAttribute("sessionId",sessionId);return "trainer/workout-form";}service.updateWorkout(u.userId(),sessionId,form);r.addFlashAttribute("message","운동 기록을 수정했습니다.");return "redirect:/trainer/workouts";}
 @GetMapping("/profile") String profile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,Model m){var p=service.profile(u.userId());m.addAttribute("profileForm",new TrainerProfileForm(p.name(),p.phone(),p.specialty()));return "trainer/profile";}
 @PostMapping("/profile") String updateProfile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser u,@Valid @ModelAttribute("profileForm") TrainerProfileForm form,BindingResult b,RedirectAttributes r){if(b.hasErrors())return "trainer/profile";service.updateProfile(u.userId(),form);r.addFlashAttribute("message","내 정보를 수정했습니다.");return "redirect:/trainer/profile";}
 private void routineForm(Model m,TrainerRoutineForm f,Long id){m.addAttribute("routineForm",f);m.addAttribute("members",service.members(id,null));}
 private void workoutForm(Model m,TrainerWorkoutForm f,Long id){m.addAttribute("workoutForm",f);m.addAttribute("members",service.members(id,null));}
}
