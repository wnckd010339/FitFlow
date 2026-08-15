package com.acorn.gymmanagement.payment.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.payment.dto.request.CreateMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.response.PaymentOrderResponse;
import com.acorn.gymmanagement.payment.service.MemberPaymentOrderService;
import com.acorn.gymmanagement.security.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/payment-orders")
@RequiredArgsConstructor
public class MemberPaymentOrderController {

    private final MemberPaymentOrderService paymentOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentOrderResponse>> create(

            @SessionAttribute(SessionUser.SESSION_KEY)
            SessionUser sessionUser,

            @Valid @RequestBody
            CreateMemberPaymentOrderRequest request
    ) {
        PaymentOrderResponse response =
                paymentOrderService.create(
                        sessionUser.userId(),
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "결제 주문을 생성했습니다.",
                        response
                ));
    }
}
