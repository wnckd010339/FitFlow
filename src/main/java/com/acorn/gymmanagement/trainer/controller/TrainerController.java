package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerService trainerService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) Boolean available,
                        Model model) {
        TrainerSearchCondition condition = new TrainerSearchCondition(keyword, status, available);
        model.addAttribute("condition", condition);
        model.addAttribute("summary", trainerService.getSummary());
        model.addAttribute("trainers", trainerService.findTrainers(condition));
        model.addAttribute("waitingMembers", trainerService.findWaitingMembers());
        return "admin/trainer/index";
    }
}
