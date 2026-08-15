package com.acorn.gymmanagement.payment.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.membership.model.PendingMembershipPaymentTarget;
import com.acorn.gymmanagement.membership.service.MembershipService;
import com.acorn.gymmanagement.payment.dto.request.CreateMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.response.PaymentOrderResponse;
import com.acorn.gymmanagement.payment.mapper.PaymentOrderMapper;
import com.acorn.gymmanagement.payment.model.PaymentOrderRegistration;
import com.acorn.gymmanagement.payment.model.PaymentOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPaymentOrderService {

    private static final Duration ORDER_EXPIRATION = Duration.ofMinutes(10);

    private static final String PG_PROVIDER =
            "TOSS_PAYMENTS";

    private final MembershipService membershipService;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentOrderExpirationService paymentOrderExpirationService;

    @Transactional
    public PaymentOrderResponse create(
            Long userId,
            CreateMemberPaymentOrderRequest request
    ){
        paymentOrderExpirationService.expireForMember(
                userId,
                LocalDateTime.now()
        );

        PendingMembershipPaymentTarget target =
                membershipService.createPendingForMember(
                        userId,
                        request.productId(),
                        request.startDate()
                );

        String orderId = createOrderId();
        String idempotencyKey = UUID.randomUUID().toString();
        LocalDateTime expiresAt =
                LocalDateTime.now().plus(ORDER_EXPIRATION);

        PaymentOrderRegistration registration =
                new PaymentOrderRegistration(
                        orderId,
                        target.memberId(),
                        target.membershipId(),
                        PG_PROVIDER,
                        target.price(),
                        PaymentOrderStatus.READY,
                        idempotencyKey,
                        expiresAt
                );

        int affectedRows =
                paymentOrderMapper.insert(registration);

        if(affectedRows != 1){
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    "결제 주문 저장에 실패했습니다."
            );
        }

        return new PaymentOrderResponse(
                orderId,
                target.productName(),
                target.price(),
                expiresAt
        );
    }

    private String createOrderId(){
        return "FITFLOW-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "");
    }



}
