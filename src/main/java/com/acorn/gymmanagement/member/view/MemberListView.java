package com.acorn.gymmanagement.member.view;

import com.acorn.gymmanagement.member.dto.response.MemberResponse;

import java.util.List;

public record MemberListView(List<MemberResponse> members, long totalCount) {
}
