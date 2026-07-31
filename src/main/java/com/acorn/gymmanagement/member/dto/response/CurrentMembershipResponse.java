package com.acorn.gymmanagement.member.dto.response;

import java.time.LocalDate;

public record CurrentMembershipResponse(
        Long membershipId,
        String membershipName,
        LocalDate startDate,
        LocalDate endDate,
        Integer remainingDays,
        Integer usagePercent,
        String status
) {
}
