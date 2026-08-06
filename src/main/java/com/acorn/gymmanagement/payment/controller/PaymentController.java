package com.acorn.gymmanagement.payment.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/admin/payments")
public class PaymentController {

    @GetMapping
    public String redirectToMembershipPayments() {
        return "redirect:/admin/memberships";
    }
}
