package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.dto.response.TrainerWorkoutView;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerWorkoutForm;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import com.acorn.gymmanagement.trainer.service.TrainerRoutineService;
import com.acorn.gymmanagement.trainer.service.TrainerWorkoutService;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trainer/workouts")
@RequiredArgsConstructor
public class TrainerWorkoutController {
    private final TrainerWorkoutService trainerWorkoutService;
    private final TrainerMemberService trainerMemberService;
    private final TrainerRoutineService trainerRoutineService;

    @GetMapping
    public String workouts(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @RequestParam(required = false) Long memberId,
            Model model) {
        Long userId = sessionUser.userId();
        model.addAttribute("members", trainerMemberService.members(userId, null));
        model.addAttribute("workoutDays", trainerWorkoutService.workoutDays(userId, memberId));
        model.addAttribute("selectedMemberId", memberId);
        return "trainer/workouts";
    }

    @GetMapping("/daily")
    public String dailyWorkouts(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @RequestParam Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        List<TrainerWorkoutView> workouts =
                trainerWorkoutService.workoutsByDate(sessionUser.userId(), memberId, date);
        if (workouts.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "해당 날짜의 운동 기록을 찾을 수 없습니다.");
        }
        model.addAttribute("memberName", workouts.get(0).memberName());
        model.addAttribute("workoutDate", date);
        model.addAttribute("workouts", workouts);
        model.addAttribute("exerciseCount", workouts.size());
        model.addAttribute("totalSets", workouts.stream()
                .mapToInt(workout -> workout.sets() == null ? 0 : workout.sets())
                .sum());
        return "trainer/workout-daily-detail";
    }

    @GetMapping("/new")
    public String newWorkout(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            Model model) {
        var exercise = new TrainerWorkoutExerciseForm("", 3, null, null);
        prepareForm(model, new TrainerWorkoutForm(null, null, 60, "", List.of(exercise)),
                sessionUser.userId());
        return "trainer/workout-form";
    }

    @PostMapping
    public String createWorkout(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @Valid @ModelAttribute TrainerWorkoutForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form, sessionUser.userId());
            return "trainer/workout-form";
        }
        trainerWorkoutService.createWorkout(sessionUser.userId(), form);
        redirectAttributes.addFlashAttribute("message", "운동 기록을 저장했습니다.");
        return "redirect:/trainer/workouts";
    }

    @GetMapping("/{sessionId}/edit")
    public String editWorkout(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long sessionId,
            Model model) {
        Long userId = sessionUser.userId();
        TrainerWorkoutView workout = trainerWorkoutService.workout(userId, sessionId);
        List<TrainerWorkoutExerciseForm> exercises = trainerWorkoutService
                .workoutExercises(userId, sessionId)
                .stream()
                .map(exercise -> new TrainerWorkoutExerciseForm(
                        exercise.exerciseName(), exercise.sets(), exercise.weight(), exercise.reps()))
                .toList();
        prepareForm(model, toWorkoutForm(workout, exercises), userId);
        model.addAttribute("sessionId", sessionId);
        return "trainer/workout-form";
    }

    @PostMapping("/{sessionId}")
    public String updateWorkout(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long sessionId,
            @Valid @ModelAttribute TrainerWorkoutForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form, sessionUser.userId());
            model.addAttribute("sessionId", sessionId);
            return "trainer/workout-form";
        }
        trainerWorkoutService.updateWorkout(sessionUser.userId(), sessionId, form);
        redirectAttributes.addFlashAttribute("message", "운동 기록을 수정했습니다.");
        return "redirect:/trainer/workouts";
    }

    @PostMapping("/{sessionId}/delete")
    public String deleteWorkout(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long sessionId,
            RedirectAttributes redirectAttributes) {
        trainerWorkoutService.deleteWorkout(sessionUser.userId(), sessionId);
        redirectAttributes.addFlashAttribute("message", "운동 기록을 삭제했습니다.");
        return "redirect:/trainer/workouts";
    }

    @PostMapping("/daily/delete")
    public String deleteWorkoutDay(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @RequestParam Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes) {
        trainerWorkoutService.deleteWorkoutDay(sessionUser.userId(), memberId, date);
        redirectAttributes.addFlashAttribute("message", "해당 날짜의 운동 기록을 모두 삭제했습니다.");
        return "redirect:/trainer/workouts";
    }

    private void prepareForm(Model model, TrainerWorkoutForm form, Long userId) {
        model.addAttribute("workoutForm", form);
        model.addAttribute("members", trainerMemberService.members(userId, null));
        model.addAttribute("routines", trainerRoutineService.routines(userId, null));
    }

    static TrainerWorkoutForm toWorkoutForm(
            TrainerWorkoutView workout, List<TrainerWorkoutExerciseForm> exercises) {
        long minutes = Duration.between(workout.startedAt(), workout.endedAt()).toMinutes();
        return new TrainerWorkoutForm(
                workout.memberId(), workout.routineId(), (int) Math.max(1, minutes),
                workout.memo(), exercises);
    }
}
