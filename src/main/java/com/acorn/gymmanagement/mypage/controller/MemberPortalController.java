package com.acorn.gymmanagement.mypage.controller;

import com.acorn.gymmanagement.membership.service.MembershipService;
import com.acorn.gymmanagement.mypage.form.WorkoutRecordForm;
import com.acorn.gymmanagement.mypage.form.WorkoutExerciseForm;
import com.acorn.gymmanagement.mypage.form.MemberProfileForm;
import com.acorn.gymmanagement.mypage.service.MemberPortalService;
import com.acorn.gymmanagement.security.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberPortalController {
    private final MemberPortalService memberPortalService;
    private final MembershipService membershipService;

    @GetMapping("/memberships")
    public String memberships(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, Model model) {
        model.addAttribute("memberships", memberPortalService.memberships(user.userId()));
        model.addAttribute("membershipProducts", membershipService.findActiveProducts());
        return "member/memberships";
    }

    @GetMapping("/attendance")
    public String attendance(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, Model model) {
        model.addAttribute("attendances", memberPortalService.attendances(user.userId()));
        return "member/attendance";
    }

    @GetMapping("/workouts")
    public String workouts(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, Model model) {
        var routine = memberPortalService.routine(user.userId());
        model.addAttribute("routine", routine);
        model.addAttribute("workoutDays", memberPortalService.workoutDays(user.userId()));
        Long routineId = routine.stream().findFirst().map(item -> item.routineId()).orElse(null);
        model.addAttribute("workoutRecordForm", new WorkoutRecordForm(
                routineId, 60, "", List.of(new WorkoutExerciseForm("", 3, null, null))
        ));
        return "member/workouts";
    }

    @PostMapping("/workouts")
    public String saveWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                              @Valid @ModelAttribute WorkoutRecordForm workoutRecordForm, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("routine", memberPortalService.routine(user.userId()));
            model.addAttribute("workoutDays", memberPortalService.workoutDays(user.userId()));
            return "member/workouts";
        }
        memberPortalService.saveWorkout(user.userId(), workoutRecordForm);
        redirectAttributes.addFlashAttribute("message", "운동 기록을 저장했습니다.");
        return "redirect:/member/workouts";
    }

    @GetMapping("/workouts/{sessionId}/edit")
    public String editWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                              @PathVariable Long sessionId, Model model) {
        var workout = memberPortalService.workoutForEdit(user.userId(), sessionId);
        var exercises = memberPortalService.workoutExercises(user.userId(), sessionId).stream()
                .map(exercise -> new WorkoutExerciseForm(exercise.exerciseName(), exercise.sets(), exercise.weight(), exercise.reps()))
                .toList();
        model.addAttribute("workoutRecordForm", new WorkoutRecordForm(
                workout.routineId(), workout.durationMinutes(), workout.memo(), exercises
        ));
        model.addAttribute("sessionId", sessionId);
        return "member/workout-edit";
    }

    @PostMapping("/workouts/{sessionId}")
    public String updateWorkout(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, @PathVariable Long sessionId,
                                @Valid @ModelAttribute WorkoutRecordForm workoutRecordForm, BindingResult bindingResult,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sessionId", sessionId);
            return "member/workout-edit";
        }
        memberPortalService.updateWorkout(user.userId(), sessionId, workoutRecordForm);
        redirectAttributes.addFlashAttribute("message", "운동 기록을 수정했습니다.");
        return "redirect:/member/workouts";
    }

    @GetMapping("/workouts/daily")
    public String workoutDaily(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                               @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {
        var workouts = memberPortalService.workoutsByDate(user.userId(), date);
        if (workouts.isEmpty()) {
            throw new com.acorn.gymmanagement.common.exception.BusinessException(
                    com.acorn.gymmanagement.common.exception.ErrorCode.NOT_FOUND,
                    "해당 날짜의 운동 기록을 찾을 수 없습니다."
            );
        }
        model.addAttribute("workoutDate", date);
        model.addAttribute("workouts", workouts);
        model.addAttribute("exerciseCount", workouts.size());
        model.addAttribute("totalSets", workouts.stream().mapToInt(workout -> workout.sets() == null ? 0 : workout.sets()).sum());
        return "member/workout-daily-detail";
    }

    @PostMapping("/workouts/daily/delete")
    public String deleteWorkoutDay(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                                   @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
                                   RedirectAttributes redirectAttributes) {
        memberPortalService.deleteWorkoutDay(user.userId(), date);
        redirectAttributes.addFlashAttribute("message", "해당 날짜의 운동 기록을 삭제했습니다.");
        return "redirect:/member/workouts";
    }

    @GetMapping("/payments")
    public String payments(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, Model model) {
        model.addAttribute("payments", memberPortalService.payments(user.userId()));
        return "member/payments";
    }

    @GetMapping("/profile")
    public String profile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user, Model model) {
        var profile = memberPortalService.profile(user.userId());
        model.addAttribute("profile", profile);
        model.addAttribute("memberProfileForm", new MemberProfileForm(
                profile.name(), profile.phone(), profile.birthDate(), profile.gender(), profile.email()
        ));
        return "member/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                                @Valid @ModelAttribute MemberProfileForm memberProfileForm,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", memberPortalService.profile(user.userId()));
            return "member/profile";
        }
        memberPortalService.updateProfile(user.userId(), memberProfileForm);
        redirectAttributes.addFlashAttribute("message", "내 정보가 수정되었습니다.");
        return "redirect:/member/profile";
    }
}
