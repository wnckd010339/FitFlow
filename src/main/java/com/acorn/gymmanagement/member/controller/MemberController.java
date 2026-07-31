package com.acorn.gymmanagement.member.controller;

import com.acorn.gymmanagement.member.dto.request.MemberSearchRequest;
import com.acorn.gymmanagement.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String list(
            @ModelAttribute MemberSearchRequest condition,
            Model model
            ) {

        model.addAttribute("members", memberService.search(condition));
        model.addAttribute("condition", condition);
        return "admin/member/list";
    }
}
