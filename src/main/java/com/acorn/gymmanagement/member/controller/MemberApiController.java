package com.acorn.gymmanagement.member.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.member.dto.request.CreateMemberRequest;
import com.acorn.gymmanagement.member.dto.request.UpdateMemberRequest;
import com.acorn.gymmanagement.member.dto.response.CreateMemberResponse;
import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;
import com.acorn.gymmanagement.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMemberResponse>> create(
            @Valid @RequestBody
            CreateMemberRequest request
    ) {
        CreateMemberResponse response =
                memberService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "신규 회원이 등록되었습니다.",
                                response
                        )
                );
    }

    @PatchMapping("/{memberId}fhr")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateBasicInformation (
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateMemberRequest request
            ){
        MemberDetailResponse response =
                memberService.updateBasicInformation(memberId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "회원 기본 정보가 수정되었습니다.",
                        response
                )
        );
    }
}
