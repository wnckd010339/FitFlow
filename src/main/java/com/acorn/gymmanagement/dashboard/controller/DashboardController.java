package com.acorn.gymmanagement.dashboard.controller;

import com.acorn.gymmanagement.dashboard.service.DashboardService;
import com.acorn.gymmanagement.security.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public String dashboard(@SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser, Model model) {
        model.addAttribute("dashboard", dashboardService.getDashboard());
        model.addAttribute("adminName", sessionUser.loginId() != null ? sessionUser.loginId() : sessionUser.email());
        return "admin/dashboard/index";
    }
}
