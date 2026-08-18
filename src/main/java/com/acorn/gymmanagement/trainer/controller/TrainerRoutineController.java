package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.dto.response.TrainerRoutineView;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineExerciseForm;
import com.acorn.gymmanagement.trainer.form.TrainerRoutineForm;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import com.acorn.gymmanagement.trainer.service.TrainerRoutineService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/trainer/routines")
@RequiredArgsConstructor
public class TrainerRoutineController {
    private final TrainerRoutineService trainerRoutineService;
    private final TrainerMemberService trainerMemberService;

    @GetMapping
    public String routines(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @RequestParam(required = false) Long memberId,
            Model model) {
        Long userId = sessionUser.userId();
        model.addAttribute("members", trainerMemberService.members(userId, null));
        model.addAttribute("routines", trainerRoutineService.routines(userId, memberId));
        model.addAttribute("selectedMemberId", memberId);
        return "trainer/routines";
    }

    @GetMapping("/new")
    public String newRoutine(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            Model model) {
        var exercise = new TrainerRoutineExerciseForm("", 3, 8, 12, null, 60, "");
        var form = new TrainerRoutineForm(
                null, "", "", LocalDate.now(), null, List.of(exercise));
        prepareForm(model, form, sessionUser.userId());
        return "trainer/routine-form";
    }

    @PostMapping
    public String createRoutine(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @Valid @ModelAttribute TrainerRoutineForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form, sessionUser.userId());
            return "trainer/routine-form";
        }
        trainerRoutineService.createRoutine(sessionUser.userId(), form);
        redirectAttributes.addFlashAttribute("message", "운동 루틴을 등록했습니다.");
        return "redirect:/trainer/routines";
    }

    @GetMapping("/{routineId}/edit")
    public String editRoutine(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long routineId,
            Model model) {
        Long userId = sessionUser.userId();
        TrainerRoutineView routine = trainerRoutineService.routine(userId, routineId);
        List<TrainerRoutineExerciseForm> exercises = trainerRoutineService
                .routineExercises(userId, routineId)
                .stream()
                .map(exercise -> new TrainerRoutineExerciseForm(
                        exercise.exerciseName(), exercise.targetSets(), exercise.targetRepsMin(),
                        exercise.targetRepsMax(), exercise.targetWeight(), exercise.restSeconds(),
                        exercise.memo()))
                .toList();
        if (exercises.isEmpty()) {
            exercises = List.of(new TrainerRoutineExerciseForm("", 3, 8, 12, null, 60, ""));
        }
        var form = new TrainerRoutineForm(
                routine.memberId(), routine.title(), routine.description(), routine.startDate(),
                routine.endDate(), exercises);
        prepareForm(model, form, userId);
        model.addAttribute("routineId", routineId);
        return "trainer/routine-form";
    }

    @PostMapping("/{routineId}")
    public String updateRoutine(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long routineId,
            @Valid @ModelAttribute TrainerRoutineForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, form, sessionUser.userId());
            model.addAttribute("routineId", routineId);
            return "trainer/routine-form";
        }
        trainerRoutineService.updateRoutine(sessionUser.userId(), routineId, form);
        redirectAttributes.addFlashAttribute("message", "운동 루틴을 수정했습니다.");
        return "redirect:/trainer/routines";
    }

    @PostMapping("/{routineId}/delete")
    public String deleteRoutine(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long routineId,
            RedirectAttributes redirectAttributes) {
        trainerRoutineService.deleteRoutine(sessionUser.userId(), routineId);
        redirectAttributes.addFlashAttribute("message", "운동 루틴을 삭제했습니다.");
        return "redirect:/trainer/routines";
    }

    private void prepareForm(Model model, TrainerRoutineForm form, Long userId) {
        model.addAttribute("routineForm", form);
        model.addAttribute("members", trainerMemberService.members(userId, null));
    }
}
