package com.acorn.gymmanagement.payment.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.membership.service.MembershipService;
import com.acorn.gymmanagement.payment.dto.request.ConfirmMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.response.PaymentResponse;
import com.acorn.gymmanagement.payment.mapper.PaymentOrderMapper;
import com.acorn.gymmanagement.payment.model.PaymentMethod;
import com.acorn.gymmanagement.payment.model.PaymentOrder;
import com.acorn.gymmanagement.payment.model.PaymentOrderStatus;
import com.acorn.gymmanagement.payment.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberPaymentOrderServiceTest {
    @Mock MembershipService membershipService;
    @Mock PaymentOrderMapper paymentOrderMapper;
    @Mock PaymentOrderExpirationService expirationService;
    @Mock PaymentService paymentService;
    MemberPaymentOrderService service;

    @BeforeEach void setUp() {
        service = new MemberPaymentOrderService(membershipService, paymentOrderMapper, expirationService, paymentService);
    }

    @Test void confirmsOwnedReadyOrderAndMarksItPaid() {
        PaymentOrder order = order(PaymentOrderStatus.READY, LocalDateTime.now().plusMinutes(5));
        when(paymentOrderMapper.findByOrderIdForUpdate("ORDER-1", 10L)).thenReturn(Optional.of(order));
        when(paymentOrderMapper.markApproving(1L, "LOCAL-ORDER-1")).thenReturn(1);
        when(paymentService.completeMembershipPayment(eq(30L), any())).thenReturn(payment());
        when(paymentOrderMapper.markPaid(eq(1L), eq(40L), any())).thenReturn(1);

        var result = service.confirm(10L, "ORDER-1", new ConfirmMemberPaymentOrderRequest(new BigDecimal("100000"), PaymentMethod.CARD));

        assertEquals(40L, result.paymentId());
        verify(paymentOrderMapper).markPaid(eq(1L), eq(40L), any());
    }

    @Test void rejectsAmountMismatchBeforePayment() {
        when(paymentOrderMapper.findByOrderIdForUpdate("ORDER-1", 10L))
                .thenReturn(Optional.of(order(PaymentOrderStatus.READY, LocalDateTime.now().plusMinutes(5))));
        assertThrows(BusinessException.class, () -> service.confirm(
                10L, "ORDER-1", new ConfirmMemberPaymentOrderRequest(new BigDecimal("1"), PaymentMethod.CARD)));
        verify(paymentService, never()).completeMembershipPayment(any(), any());
    }

    @Test void rejectsExpiredOrder() {
        when(paymentOrderMapper.findByOrderIdForUpdate("ORDER-1", 10L))
                .thenReturn(Optional.of(order(PaymentOrderStatus.READY, LocalDateTime.now().minusSeconds(1))));
        assertThrows(BusinessException.class, () -> service.confirm(
                10L, "ORDER-1", new ConfirmMemberPaymentOrderRequest(new BigDecimal("100000"), PaymentMethod.CARD)));
    }

    private PaymentOrder order(PaymentOrderStatus status, LocalDateTime expiresAt) {
        return new PaymentOrder(1L, "ORDER-1", 20L, 30L, null, "LOCAL", new BigDecimal("100000"),
                status, null, "key", expiresAt, null);
    }

    private PaymentResponse payment() {
        return new PaymentResponse(40L, 20L, 30L, "회원", "상품", new BigDecimal("100000"), PaymentMethod.CARD,
                PaymentStatus.COMPLETED, LocalDateTime.now(), BigDecimal.ZERO, new BigDecimal("100000"));
    }
}
