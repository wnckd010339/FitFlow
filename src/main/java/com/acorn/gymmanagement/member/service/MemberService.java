package com.acorn.gymmanagement.member.service;

import com.acorn.gymmanagement.member.dto.request.MemberSearchRequest;
import com.acorn.gymmanagement.member.dto.response.MemberListResponse;
import com.acorn.gymmanagement.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberMapper memberMapper;

    public List<MemberListResponse> search(MemberSearchRequest condition){
        return memberMapper.search(condition);
    }
}
