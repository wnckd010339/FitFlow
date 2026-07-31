package com.acorn.gymmanagement.member.mapper;

import com.acorn.gymmanagement.member.dto.request.MemberSearchRequest;
import com.acorn.gymmanagement.member.dto.response.MemberListResponse;
import com.acorn.gymmanagement.member.model.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MemberMapper {

    List<MemberListResponse> search(MemberSearchRequest condition);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    int insert(Member member);

    int update(Member member);
}
