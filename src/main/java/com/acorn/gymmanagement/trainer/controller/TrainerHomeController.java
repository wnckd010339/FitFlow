package com.acorn.gymmanagement.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trainer/home")
public class TrainerHomeController {

    @GetMapping
    public String home() {
        // TODO: 트레이너 Service 구현 후 하드코딩 화면 데이터를 교체합니다.
        return "trainer/home";
    }
}
