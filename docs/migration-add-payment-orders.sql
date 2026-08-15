-- Existing FitFlow databases: add the member PG payment order table.
-- Run after payments and member_memberships have been created.

CREATE TABLE payment_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(64) NOT NULL,
    member_id BIGINT NOT NULL,
    member_membership_id BIGINT NOT NULL,
    payment_id BIGINT NULL,
    pg_provider VARCHAR(30) NOT NULL DEFAULT 'TOSS_PAYMENTS',
    amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'READY',
    payment_key VARCHAR(200) NULL,
    idempotency_key VARCHAR(300) NOT NULL,
    failure_code VARCHAR(100) NULL,
    failure_message VARCHAR(500) NULL,
    expires_at DATETIME NOT NULL,
    approved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_payment_order_order_id UNIQUE (order_id),
    CONSTRAINT uk_payment_order_payment_id UNIQUE (payment_id),
    CONSTRAINT uk_payment_order_payment_key UNIQUE (payment_key),
    CONSTRAINT uk_payment_order_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT fk_payment_order_member FOREIGN KEY (member_id) REFERENCES members (id),
    CONSTRAINT fk_payment_order_membership FOREIGN KEY (member_membership_id) REFERENCES member_memberships (id),
    CONSTRAINT fk_payment_order_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
    CONSTRAINT ck_payment_order_amount CHECK (amount > 0),
    CONSTRAINT ck_payment_order_status CHECK (
        status IN ('READY', 'APPROVING', 'PAID', 'FAILED', 'CANCELLED', 'EXPIRED')
    ),
    INDEX ix_payment_orders_member_created_at (member_id, created_at),
    INDEX ix_payment_orders_membership (member_membership_id),
    INDEX ix_payment_orders_status_expires_at (status, expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
