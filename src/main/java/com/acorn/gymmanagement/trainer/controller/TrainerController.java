package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.service.TrainerAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.dto.request.AssignTrainerRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerAdminService trainerService;

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
        model.addAttribute("assignedMembers", trainerService.findAssignedMembers());
        return "admin/trainer/index";
    }

    @PostMapping("/assignments")
    public String assign(@Valid @ModelAttribute AssignTrainerRequest request,
                         @SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                         RedirectAttributes redirectAttributes) {
        trainerService.assign(request, user.userId());
        redirectAttributes.addFlashAttribute("message", "트레이너가 배정되었습니다.");
        return "redirect:/admin/trainers";
    }

    @PostMapping("/assignments/{memberId}/end")
    public String unassign(@PathVariable Long memberId, RedirectAttributes redirectAttributes) {
        trainerService.unassign(memberId, java.time.LocalDate.now());
        redirectAttributes.addFlashAttribute("message", "트레이너 배정이 해제되었습니다.");
        return "redirect:/admin/trainers";
    }
}
