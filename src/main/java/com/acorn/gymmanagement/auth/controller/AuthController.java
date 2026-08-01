package com.acorn.gymmanagement.auth.controller;

import com.acorn.gymmanagement.auth.form.LoginForm;
import com.acorn.gymmanagement.auth.service.AuthService;
import com.acorn.gymmanagement.auth.service.SessionService;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.security.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    @GetMapping("/login")
    public String loginForm(
            HttpSession httpSession,
            @RequestParam(required = false) String redirect,
            Model model
    ){
        SessionUser sessionUser = sessionService
                .getUser(httpSession)
                .orElse(null);

        if(sessionUser != null){
            return "redirect:" + sessionUser.defaultRedirectPath();
        }

        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            HttpServletRequest request,
            HttpSession httpSession,
            @Valid @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            @RequestParam(required = false) String redirect,
            Model model
    ){
        if(bindingResult.hasErrors()){
            model.addAttribute("redirect", redirect);
            return "auth/login";
        }

        try{
            authService.login(httpSession, loginForm);
            request.changeSessionId();
        } catch (BusinessException exception){
            model.addAttribute("redirect", redirect);
            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "auth/login";
        }

        SessionUser sessionUser = sessionService
                .getUser(httpSession)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "로그인 후 세션 사용자 정보가 없습니다."
                        ));

        String destination = safeRedirect(
                redirect,
                sessionUser
        );

        return "redirect:" + destination;
    }

    @PostMapping("/logout")
    public String logout(HttpSession httpSession){
        sessionService.logout(httpSession);

        return "redirect:/login";
    }

    private String safeRedirect(
            String redirect,
            SessionUser sessionUser
    ){
        if(redirect == null || redirect.isBlank()){
            return sessionUser.defaultRedirectPath();
        }

        if(!redirect.startsWith("/")
                ||redirect.startsWith("//")
                ||redirect.contains("\\")
                ||redirect.contains("\r")
                ||redirect.contains("\n")){
            return sessionUser.defaultRedirectPath();
        }
        String path = redirect.split("\\?", 2)[0];

        if(!sessionUser.canAccess(path)){
            return sessionUser.defaultRedirectPath();
        }

        return redirect;
    }
}
