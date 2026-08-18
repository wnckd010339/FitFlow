package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/trainer/home")
@RequiredArgsConstructor
public class TrainerHomeController {
    private final TrainerMemberService trainerMemberService;

    @GetMapping
    public String home(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser, Model model) {
        model.addAttribute("profile", trainerMemberService.homeProfile(sessionUser.userId()));
        model.addAttribute("members", trainerMemberService.homeMembers(sessionUser.userId()));
        return "trainer/home";
    }
}
