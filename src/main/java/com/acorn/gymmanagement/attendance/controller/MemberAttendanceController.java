package com.acorn.gymmanagement.attendance.controller;

import com.acorn.gymmanagement.attendance.service.AttendanceService;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.security.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member/attendance")
@RequiredArgsConstructor
public class MemberAttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public String checkIn(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            attendanceService.checkInMember(sessionUser.userId());
            redirectAttributes.addFlashAttribute("message", "체크인이 완료되었습니다.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/member/home";
    }

    @PostMapping("/check-out")
    public String checkOut(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            RedirectAttributes redirectAttributes
    ) {
        try {
            attendanceService.checkoutMember(sessionUser.userId());
            redirectAttributes.addFlashAttribute("message", "체크아웃이 완료되었습니다.");
        } catch (BusinessException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/member/home";
    }
}
