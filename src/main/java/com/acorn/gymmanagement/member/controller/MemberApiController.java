package com.acorn.gymmanagement.member.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    @GetMapping
    public ApiResponse<List<Object>> list() {
        return ApiResponse.success("회원 목록 조회 준비가 완료되었습니다.", List.of());
    }
}
