package com.acorn.gymmanagement.auth.controller;

import com.acorn.gymmanagement.auth.form.SignupForm;
import com.acorn.gymmanagement.auth.service.SessionService;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SignupController {

    private final MemberService memberService;
    private final SessionService sessionService;

    @GetMapping("/signup")
    public String signupForm(HttpSession httpSession, Model model) {
        var sessionUser = sessionService.getUser(httpSession).orElse(null);
        if (sessionUser != null) {
            return "redirect:" + sessionUser.defaultRedirectPath();
        }

        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signupForm") SignupForm signupForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (!signupForm.passwordsMatch()) {
            bindingResult.rejectValue(
                    "passwordConfirmation",
                    "password.mismatch",
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다."
            );
        }
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            memberService.create(signupForm.toCreateMemberRequest());
        } catch (BusinessException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "auth/signup";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "회원가입이 완료되었습니다. 가입한 계정으로 로그인해 주세요."
        );
        return "redirect:/login";
    }
}
