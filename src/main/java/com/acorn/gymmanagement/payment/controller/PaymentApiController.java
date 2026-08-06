package com.acorn.gymmanagement.payment.controller;

import com.acorn.gymmanagement.common.response.ApiResponse;
import com.acorn.gymmanagement.payment.dto.request.CreateRefundRequest;
import com.acorn.gymmanagement.payment.dto.response.PaymentHistoryResponse;
import com.acorn.gymmanagement.payment.dto.response.RefundResponse;
import com.acorn.gymmanagement.payment.service.PaymentService;
import com.acorn.gymmanagement.security.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @GetMapping("/api/payments")
    public ApiResponse<List<PaymentHistoryResponse>> findHistory(
            @RequestParam(required = false) Long memberId
    ) {
        return ApiResponse.success(
                "결제·환불 내역을 조회했습니다.",
                paymentService.findHistory(memberId)
        );
    }

    @PostMapping("/api/payments/{paymentId}/refunds")
    public ResponseEntity<ApiResponse<RefundResponse>> refund(
            @PathVariable Long paymentId,
            @Valid @RequestBody CreateRefundRequest request,
            @SessionAttribute(SessionUser.SESSION_KEY) SessionUser sessionUser
    ) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "환불이 완료되었습니다.",
                        paymentService.refund(paymentId, request, sessionUser.userId())
                ));
    }
}
