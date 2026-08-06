package com.acorn.gymmanagement.membership.controller;

import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;
import com.acorn.gymmanagement.member.service.MemberService;
import com.acorn.gymmanagement.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/memberships")
public class MembershipController {

    private final MemberService memberService;
    private final PaymentService paymentService;

    @GetMapping
    public String index(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "history") String view,
            Model model
    ) {
        MemberDetailResponse selectedMember = memberId == null
                ? null
                : memberService.findDetailResponseById(memberId);

        model.addAttribute("selectedMember", selectedMember);
        model.addAttribute("selectedView", "history");
        model.addAttribute("memberFiltered", selectedMember != null);
        model.addAttribute("members", paymentService.findActiveMembers());
        return "admin/membership/index";
    }
}
