package com.acorn.gymmanagement.member.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.common.pagination.PageRequest;
import com.acorn.gymmanagement.common.pagination.PageResult;
import com.acorn.gymmanagement.member.dto.request.MemberSearchRequest;
import com.acorn.gymmanagement.member.dto.response.CurrentMembershipResponse;
import com.acorn.gymmanagement.member.dto.response.MemberActivityResponse;
import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;
import com.acorn.gymmanagement.member.dto.response.MemberListResponse;
import com.acorn.gymmanagement.member.mapper.MemberMapper;
import com.acorn.gymmanagement.member.view.MemberDetailView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberMapper memberMapper;



    public PageResult<MemberListResponse> search(
            MemberSearchRequest condition,
            PageRequest pageRequest
    ){
        List<MemberListResponse> members =
                memberMapper.search(condition, pageRequest);

        long totalElements =
                memberMapper.count(condition);

        return PageResult.of(
                members,
                totalElements,
                pageRequest
        );
    }

    public MemberDetailResponse findDetailResponseById(Long memberId){
        return memberMapper.findDetailById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "회원을 찾을 수 없습니다."
                ));
    }

    public MemberDetailView findDetailView(Long memberId){
        MemberDetailResponse member = memberMapper.findDetailById(memberId)
                .orElseThrow( () -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "회원을 찾을 수 없습니다."
                ));

        CurrentMembershipResponse membership =
                memberMapper.findCurrentMembership(memberId)
                        .orElse(null);

        List<MemberActivityResponse> activities =
                memberMapper.findRecentActivities(memberId);

        return new MemberDetailView(
                member,
                membership,
                activities
        );
    }
}
