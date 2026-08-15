package com.acorn.gymmanagement.payment.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.membership.mapper.MembershipMapper;
import com.acorn.gymmanagement.membership.model.MembershipStatus;
import com.acorn.gymmanagement.payment.mapper.PaymentOrderMapper;
import com.acorn.gymmanagement.payment.model.ExpiredPaymentOrderTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentOrderExpirationService {

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final PaymentOrderMapper paymentOrderMapper;
    private final MembershipMapper membershipMapper;

    @Transactional
    public int expireBatch(LocalDateTime now){

        List<ExpiredPaymentOrderTarget> targets =
                paymentOrderMapper.findExpiredReadyOrdersForUpdate(
                        now,
                        DEFAULT_BATCH_SIZE
                );

        return expireTargets(targets);
    }

    @Transactional
    public int expireForMember(
            Long userId,
            LocalDateTime now
    ) {
        List<ExpiredPaymentOrderTarget> targets =
                paymentOrderMapper.findExpiredReadyOrdersByUserIdForUpdate(
                        userId,
                        now
                );

        return expireTargets(targets);
    }

    private int expireTargets(
            List<ExpiredPaymentOrderTarget> targets
    ) {
        int expiredCount = 0;

        for(ExpiredPaymentOrderTarget target : targets) {
            expireTarget(target);
            expiredCount++;
        }

        return expiredCount;
    }

    private void expireTarget(
            ExpiredPaymentOrderTarget target
    ) {
        if(target.membershipStatus()
                == MembershipStatus.PENDING_PAYMENT) {

            int cancelled =
                    membershipMapper.cancelPendingMembership(
                            target.memberId(),
                            target.membershipId()
                    );

            if (cancelled != 1){
                throw new BusinessException(
                        ErrorCode.CONFLICT,
                        "만료 주문의 회원권 취소에 실패했습니다."
                );
            }
        } else if (target.membershipStatus()
                    != MembershipStatus.CANCELLED) {
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "결제 대기 상태가 아닌 회원권은 자동 취소할 수 없습니다."
            );
        }

        int expired =
                paymentOrderMapper.markExpired(
                        target.paymentOrderId()
                );

        if (expired != 1){
            throw new BusinessException(
                    ErrorCode.CONFLICT,
                    "결제 주문 만료 처리에 실패했습니다."
            );
        }
    }
}
