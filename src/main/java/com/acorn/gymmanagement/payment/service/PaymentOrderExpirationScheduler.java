package com.acorn.gymmanagement.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
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
    private final DataSource dataSource;
    private Boolean paymentOrdersTableAvailable;

    @Scheduled(
            fixedDelayString =
                    "${payment.order.expiration-delay-ms:60000}"
    )
    public void expirePaymentOrders() {
        if (!isPaymentOrdersTableAvailable()) {
            return;
        }

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

    private boolean isPaymentOrdersTableAvailable() {
        if (paymentOrdersTableAvailable != null) {
            return paymentOrdersTableAvailable;
        }

        try (var connection = dataSource.getConnection();
             var tables = connection.getMetaData().getTables(
                     connection.getCatalog(), null, "payment_orders", new String[]{"TABLE"})) {
            paymentOrdersTableAvailable = tables.next();
        } catch (SQLException exception) {
            paymentOrdersTableAvailable = false;
            log.warn("결제 주문 테이블 확인에 실패해 자동 만료 작업을 건너뜁니다.", exception);
        }

        if (!paymentOrdersTableAvailable) {
            log.warn("payment_orders 테이블이 없어 결제 주문 자동 만료 작업을 비활성화합니다. 결제 주문 마이그레이션 적용 후 애플리케이션을 재시작해 주세요.");
        }
        return paymentOrdersTableAvailable;
    }
}
