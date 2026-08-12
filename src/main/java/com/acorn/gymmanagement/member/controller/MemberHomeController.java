package com.acorn.gymmanagement.member.controller;

import com.acorn.gymmanagement.member.service.MemberService;
import com.acorn.gymmanagement.security.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/member/home")
@RequiredArgsConstructor
public class MemberHomeController {

    private final MemberService memberService;

    @GetMapping
    public String home(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            Model model
    ) {
        model.addAttribute("home", memberService.findHomeView(sessionUser.userId()));
        return "member/home";
    }
}
