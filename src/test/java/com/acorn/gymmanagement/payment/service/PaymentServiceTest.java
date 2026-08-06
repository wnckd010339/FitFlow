package com.acorn.gymmanagement.payment.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.membership.mapper.MembershipMapper;
import com.acorn.gymmanagement.membership.model.MembershipStatus;
import com.acorn.gymmanagement.payment.dto.request.CreatePaymentRequest;
import com.acorn.gymmanagement.payment.dto.response.PaymentResponse;
import com.acorn.gymmanagement.payment.dto.response.PaymentTargetResponse;
import com.acorn.gymmanagement.payment.mapper.PaymentMapper;
import com.acorn.gymmanagement.payment.model.PaymentMethod;
import com.acorn.gymmanagement.payment.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private MembershipMapper membershipMapper;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentMapper, membershipMapper);
    }

    @Test
    void completePaymentStoresPaymentAndActivatesMembership() {
        PaymentTargetResponse target = target(MembershipStatus.PENDING_PAYMENT);
        PaymentResponse response = paymentResponse();

        when(paymentMapper.findPaymentTargetForUpdate(1L, 10L)).thenReturn(Optional.of(target));
        when(paymentMapper.existsCompletedPaymentByMembershipId(10L)).thenReturn(false);
        when(paymentMapper.insertPayment(any())).thenAnswer(invocation -> {
            invocation.<com.acorn.gymmanagement.payment.model.PaymentRegistration>getArgument(0)
                    .setPaymentId(100L);
            return 1;
        });
        when(membershipMapper.activateAfterPayment(1L, 10L)).thenReturn(1);
        when(paymentMapper.findById(100L)).thenReturn(Optional.of(response));

        PaymentResponse result = paymentService.completeMembershipPayment(
                1L,
                10L,
                new CreatePaymentRequest(PaymentMethod.CARD)
        );

        assertEquals(100L, result.paymentId());
        verify(paymentMapper).insertPayment(any());
        verify(membershipMapper).activateAfterPayment(1L, 10L);
    }

    @Test
    void completePaymentRejectsNonPendingMembership() {
        when(paymentMapper.findPaymentTargetForUpdate(1L, 10L))
                .thenReturn(Optional.of(target(MembershipStatus.ACTIVE)));

        assertThrows(
                BusinessException.class,
                () -> paymentService.completeMembershipPayment(
                        1L,
                        10L,
                        new CreatePaymentRequest(PaymentMethod.CARD)
                )
        );

        verify(paymentMapper, never()).insertPayment(any());
    }

    @Test
    void completePaymentRejectsDuplicatePayment() {
        when(paymentMapper.findPaymentTargetForUpdate(1L, 10L))
                .thenReturn(Optional.of(target(MembershipStatus.PENDING_PAYMENT)));
        when(paymentMapper.existsCompletedPaymentByMembershipId(10L)).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> paymentService.completeMembershipPayment(
                        1L,
                        10L,
                        new CreatePaymentRequest(PaymentMethod.CASH)
                )
        );

        verify(paymentMapper, never()).insertPayment(any());
    }

    private PaymentTargetResponse target(MembershipStatus status) {
        return new PaymentTargetResponse(
                10L,
                1L,
                "테스트 회원",
                "010-0000-0000",
                3L,
                "3개월 자유 이용권",
                new BigDecimal("180000.00"),
                LocalDate.of(2026, 8, 6),
                LocalDate.of(2026, 11, 3),
                status
        );
    }

    private PaymentResponse paymentResponse() {
        return new PaymentResponse(
                100L,
                1L,
                10L,
                "테스트 회원",
                "3개월 자유 이용권",
                new BigDecimal("180000.00"),
                PaymentMethod.CARD,
                PaymentStatus.COMPLETED,
                LocalDateTime.of(2026, 8, 6, 10, 0),
                BigDecimal.ZERO,
                new BigDecimal("180000.00")
        );
    }
}
