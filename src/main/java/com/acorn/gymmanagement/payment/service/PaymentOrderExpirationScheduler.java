package com.acorn.gymmanagement.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "payment.order",
        name = "expiration-enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PaymentOrderExpirationScheduler {

    private final PaymentOrderExpirationService expirationService;

    @Scheduled(
            fixedDelayString =
                    "${payment.order.expiration-delay-ms:60000}"
    )
    public void expirePaymentOrders() {
        int expiredCount = expirationService.expireBatch(
                LocalDateTime.now()
        );

        if (expiredCount > 0) {
            log.info(
                    "만료 결제 주문 {}건을 정리했습니다.",
                    expiredCount
            );
        }
    }
}
