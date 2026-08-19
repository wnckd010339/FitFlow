package com.acorn.gymmanagement.payment.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.payment.dto.request.CreateMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.request.ConfirmMemberPaymentOrderRequest;
import com.acorn.gymmanagement.payment.dto.response.MemberPaymentConfirmationResponse;
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

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<MemberPaymentConfirmationResponse> confirm(
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser,
            @PathVariable String orderId,
            @Valid @RequestBody ConfirmMemberPaymentOrderRequest request
    ) {
        return ApiResponse.success(
                "결제가 완료되어 회원권이 활성화되었습니다.",
                paymentOrderService.confirm(sessionUser.userId(), orderId, request)
        );
    }
}
