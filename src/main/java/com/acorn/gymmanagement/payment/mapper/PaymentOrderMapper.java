package com.acorn.gymmanagement.payment.mapper;

import com.acorn.gymmanagement.payment.model.PaymentOrder;
import com.acorn.gymmanagement.payment.model.PaymentOrderRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface PaymentOrderMapper {

    int insert(PaymentOrderRegistration registration);

    Optional<PaymentOrder> findByOrderIdForUpdate(
            @Param("orderId") String orderId,
            @Param("userId") Long userId
    );

    int markApproving(
            @Param("id") Long id,
            @Param("paymentKey") String paymentKey
    );

    int markPaid(
            @Param("id") Long id,
            @Param("paymentId") Long paymentId,
            @Param("approvedAt") LocalDateTime approvedAt
    );

    int markFailed(
            @Param("id") Long id,
            @Param("failureCode") String failureCode,
            @Param("failureMessage") String failureMessage
    );

    int expireReadyOrders(
            @Param("now") LocalDateTime now
    );
}
