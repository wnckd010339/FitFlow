package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import com.acorn.gymmanagement.trainer.service.TrainerRoutineService;
import com.acorn.gymmanagement.trainer.service.TrainerWorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/trainer/members")
@RequiredArgsConstructor
public class TrainerMemberController {
    private final TrainerMemberService trainerMemberService;
    private final TrainerRoutineService trainerRoutineService;
    private final TrainerWorkoutService trainerWorkoutService;

    @GetMapping
    public String members(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @RequestParam(required = false) String keyword,
            Model model) {
        model.addAttribute("members", trainerMemberService.members(sessionUser.userId(), keyword));
        model.addAttribute("keyword", keyword);
        return "trainer/members";
    }

    @GetMapping("/{memberId}")
    public String member(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable Long memberId,
            Model model) {
        Long userId = sessionUser.userId();
        model.addAttribute("member", trainerMemberService.member(userId, memberId));
        model.addAttribute("attendances", trainerMemberService.attendances(userId, memberId));
        model.addAttribute("workoutDays", trainerWorkoutService.workoutDays(userId, memberId));
        model.addAttribute("routines", trainerRoutineService.routines(userId, memberId));
        return "trainer/member-detail";
    }
}
