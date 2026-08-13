package com.acorn.gymmanagement.mypage.dto.response;

import java.time.LocalDateTime;

public record MemberWorkoutView(Long sessionId, String routineTitle, LocalDateTime startedAt,
                                LocalDateTime endedAt, String memo, Long exerciseCount) {}
