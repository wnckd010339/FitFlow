package com.acorn.gymmanagement.membership.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.membership.dto.response.MembershipProductOptionResponse;
import com.acorn.gymmanagement.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/membership-products")
public class MembershipProductApiController {

    private final MembershipService membershipService;

    @GetMapping
    public ApiResponse<List<MembershipProductOptionResponse>> findActiveProducts() {
        return ApiResponse.success(
                "등록 가능한 회원권 상품을 조회했습니다.",
                membershipService.findActiveProducts()
        );
    }
}
