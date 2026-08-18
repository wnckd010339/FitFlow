package com.acorn.gymmanagement.trainer.controller;

import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.trainer.form.TrainerProfileForm;
import com.acorn.gymmanagement.trainer.service.TrainerMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trainer/profile")
@RequiredArgsConstructor
public class TrainerProfileController {
    private final TrainerMemberService trainerMemberService;

    @GetMapping
    public String profile(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            Model model) {
        var profile = trainerMemberService.profile(sessionUser.userId());
        model.addAttribute("profileForm",
                new TrainerProfileForm(profile.name(), profile.phone(), profile.specialty()));
        return "trainer/profile";
    }

    @PostMapping
    public String updateProfile(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @Valid @ModelAttribute("profileForm") TrainerProfileForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "trainer/profile";
        }
        trainerMemberService.updateProfile(sessionUser.userId(), form);
        redirectAttributes.addFlashAttribute("message", "내 정보를 수정했습니다.");
        return "redirect:/trainer/profile";
    }
}
