package com.acorn.gymmanagement.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member/home")
public class MemberHomeController {

    @GetMapping
    public String home() {
        // TODO: 회원 Service 구현 후 하드코딩 화면 데이터를 교체합니다.
        return "member/home";
    }
}
