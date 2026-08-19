package com.acorn.gymmanagement.payment.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.membership.model.PendingMembershipPaymentTarget;
import com.acorn.gymmanagement.membership.service.MembershipService;
import com.acorn.gymmanagement.payment.dto.request.CreateMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.request.ConfirmMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.request.CreatePaymentRequest;
import com.acorn.gymmanagement.payment.dto.response.MemberPaymentConfirmationResponse;
import com.acorn.gymmanagement.payment.dto.response.PaymentResponse;
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
    private final PaymentService paymentService;

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

    @Transactional
    public MemberPaymentConfirmationResponse confirm(
            Long userId,
            String orderId,
            ConfirmMemberPaymentOrderRequest request
    ) {
        var order = paymentOrderMapper.findByOrderIdForUpdate(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "결제 주문을 찾을 수 없습니다."));

        if (order.status() != PaymentOrderStatus.READY) {
            throw new BusinessException(ErrorCode.CONFLICT, "결제 대기 상태의 주문만 완료할 수 있습니다.");
        }
        if (order.expiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.CONFLICT, "결제 주문의 유효시간이 만료되었습니다.");
        }
        if (order.amount().compareTo(request.amount()) != 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "주문 금액과 결제 금액이 일치하지 않습니다.");
        }

        String localPaymentKey = "LOCAL-" + order.orderId();
        if (paymentOrderMapper.markApproving(order.id(), localPaymentKey) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "결제 주문 상태가 변경되어 처리하지 못했습니다.");
        }

        PaymentResponse payment = paymentService.completeMembershipPayment(
                order.membershipId(),
                new CreatePaymentRequest(order.membershipId(), request.paymentMethod())
        );
        LocalDateTime approvedAt = LocalDateTime.now();
        if (paymentOrderMapper.markPaid(order.id(), payment.paymentId(), approvedAt) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "결제 주문 완료 상태를 저장하지 못했습니다.");
        }

        return new MemberPaymentConfirmationResponse(
                order.orderId(), payment.paymentId(), order.amount(), request.paymentMethod(), approvedAt
        );
    }



}
