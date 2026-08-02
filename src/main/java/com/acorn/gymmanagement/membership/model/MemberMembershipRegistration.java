package com.acorn.gymmanagement.membership.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberMembershipRegistration {
    private Long membershipId;
    private final Long memberId;
    private final Long productId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer remainingPtSessions;
    private final MembershipStatus status;

    public MemberMembershipRegistration(
            Long memberId,
            Long productId,
            LocalDate startDate,
            LocalDate endDate,
            Integer remainingPtSessions,
            MembershipStatus status
    ) {
        this.memberId = memberId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remainingPtSessions = remainingPtSessions;
        this.status = status;
    }

    public void setMembershipId(Long membershipId){
        this.membershipId = membershipId;
    }
}
