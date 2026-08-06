package com.acorn.gymmanagement.trainer.dto.response;

import java.time.LocalDate;

public record WaitingMemberResponse(Long memberId, String memberName, String phone, LocalDate joinedAt) { }
