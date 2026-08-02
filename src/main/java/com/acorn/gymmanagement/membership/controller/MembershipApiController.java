package com.acorn.gymmanagement.membership.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.membership.dto.request.CreateMemberMembershipRequest;
import com.acorn.gymmanagement.membership.dto.response.MemberMembershipResponse;
import com.acorn.gymmanagement.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/memberships")
public class MembershipApiController {

    private final MembershipService membershipService;

    @GetMapping
    public ApiResponse<List<MemberMembershipResponse>> findAll(
            @PathVariable Long memberId
    ) {
        return ApiResponse.success(
                "회원권 이력을 조회했습니다.",
                membershipService.findAllByMemberId(memberId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberMembershipResponse>> create(
            @PathVariable Long memberId,
            @Valid @RequestBody CreateMemberMembershipRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "회원권이 등록되었습니다.",
                        membershipService.create(memberId, request)
                ));
    }

    @PatchMapping("/{membershipId}/pause")
    public ApiResponse<MemberMembershipResponse> pause(
            @PathVariable Long memberId,
            @PathVariable Long membershipId
    ) {
        return ApiResponse.success(
                "회원권이 일시정지되었습니다.",
                membershipService.pause(memberId, membershipId)
        );
    }

    @PatchMapping("/{membershipId}/resume")
    public ApiResponse<MemberMembershipResponse> resume(
            @PathVariable Long memberId,
            @PathVariable Long membershipId
    ) {
        return ApiResponse.success(
                "회원권 이용이 재개되었습니다.",
                membershipService.resume(memberId, membershipId)
        );
    }

    @PatchMapping("/{membershipId}/cancel")
    public ApiResponse<MemberMembershipResponse> cancel(
            @PathVariable Long memberId,
            @PathVariable Long membershipId
    ) {
        return ApiResponse.success(
                "회원권이 취소되었습니다.",
                membershipService.cancel(memberId, membershipId)
        );
    }
}
