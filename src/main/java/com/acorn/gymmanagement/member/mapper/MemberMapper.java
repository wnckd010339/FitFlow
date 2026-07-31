package com.acorn.gymmanagement.member.mapper;

import com.acorn.gymmanagement.common.pagination.PageRequest;
import com.acorn.gymmanagement.member.dto.request.MemberSearchRequest;
import com.acorn.gymmanagement.member.dto.response.CurrentMembershipResponse;
import com.acorn.gymmanagement.member.dto.response.MemberActivityResponse;
import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;
import com.acorn.gymmanagement.member.dto.response.MemberListResponse;
import com.acorn.gymmanagement.member.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    List<MemberListResponse> search(
            @Param("condition") MemberSearchRequest condition,
            @Param("page")PageRequest pageRequest
            );

    long count(@Param("condition") MemberSearchRequest condition);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    int insert(Member member);

    int update(Member member);

    Optional<MemberDetailResponse> findDetailById(
            @Param("memberId") Long memberId
    );

    Optional<CurrentMembershipResponse> findCurrentMembership(
            @Param("memberId") Long memberId
    );

    List<MemberActivityResponse> findRecentActivities(
            @Param("memberId") Long memberId
    );
}
