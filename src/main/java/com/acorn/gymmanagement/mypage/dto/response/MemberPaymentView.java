package com.acorn.gymmanagement.mypage.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemberPaymentView(Long transactionId, String transactionType, String productName,
                                String paymentMethod, BigDecimal amount, String status,
                                LocalDateTime occurredAt) {}
