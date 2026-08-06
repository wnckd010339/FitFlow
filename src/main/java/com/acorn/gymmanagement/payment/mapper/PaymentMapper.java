package com.acorn.gymmanagement.payment.mapper;
import com.acorn.gymmanagement.payment.dto.response.*;
import com.acorn.gymmanagement.payment.model.PaymentRegistration;
import com.acorn.gymmanagement.payment.model.Payment;
import com.acorn.gymmanagement.payment.model.PaymentStatus;
import com.acorn.gymmanagement.payment.model.RefundRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.List;

@Mapper
public interface PaymentMapper {

    Optional<PaymentTargetResponse> findPaymentTargetForUpdate(
            @Param("memberId") Long memberId,
            @Param("membershipId") Long membershipId
    );

    boolean existsCompletedPaymentByMembershipId(
            @Param("membershipId") Long membershipId
    );

    int insertPayment(PaymentRegistration registration);

    Optional<PaymentResponse> findById(
            @Param("paymentId") Long paymentId
    );

    Optional<Payment> findPaymentForUpdate(
            @Param("paymentId") Long paymentId
    );

    List<PaymentHistoryResponse> findHistory(
            @Param("memberId") Long memberId
    );

    List<PaymentMemberOptionResponse> findActiveMembers();

    int insertRefund(RefundRegistration registration);

    Optional<RefundResponse> findRefundById(
            @Param("refundId") Long refundId
    );

    int updatePaymentStatus(
            @Param("paymentId") Long paymentId,
            @Param("status") PaymentStatus status
    );
}
