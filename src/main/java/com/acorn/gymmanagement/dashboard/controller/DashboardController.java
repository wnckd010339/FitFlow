package com.acorn.gymmanagement.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @GetMapping
    public String dashboard() {
        // TODO: 대시보드 Service 구현 후 하드코딩 화면 데이터를 교체합니다.
        return "admin/dashboard/index";
    }
}
