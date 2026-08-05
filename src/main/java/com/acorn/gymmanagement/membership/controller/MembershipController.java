package com.acorn.gymmanagement.membership.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/memberships")
public class MembershipController {

    @GetMapping
    public String index() {
        return "admin/membership/index";
    }
}
