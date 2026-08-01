package com.acorn.gymmanagement.member.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.member.dto.request.CreateMemberRequest;
import com.acorn.gymmanagement.member.dto.response.CreateMemberResponse;
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
}
