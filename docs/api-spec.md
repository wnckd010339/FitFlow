# API 스켈레톤

## 공통 응답

- 성공: `success`, `message`, `data`
- 실패: `success`, `message`, `error.code`, `error.detail`

## 초기 경로

- `POST /api/auth/login`
- `GET/POST /api/members`
- `GET/PUT /api/members/{memberId}`
- `POST /api/payments`
- `POST /api/payments/{paymentId}/refunds`

## 회원권 결제

- `GET /api/payments`
  - 전체 결제·환불 내역을 최신순으로 조회합니다.
- `GET /api/payments?memberId={memberId}`
  - 특정 회원의 결제·환불 내역을 조회합니다.
- `POST /api/payments/{paymentId}/refunds`
  - 완료된 결제를 부분 또는 전액 환불합니다.
  - 요청: `amount`, `reason`
  - 전액 환불 시 결제 상태는 `REFUNDED`, 회원권은 `CANCELLED`가 됩니다.

결제·환불 API는 관리자만 접근할 수 있습니다.

### 회원 결제 주문

- `POST /api/member/payment-orders`
  - 로그인 회원 본인의 회원권 결제 주문을 생성합니다.
  - 요청: `productId`, `startDate`
  - 가격은 요청값이 아니라 서버의 활성 회원권 상품 가격으로 결정합니다.
  - 회원권은 `PENDING_PAYMENT`, 주문은 `READY` 상태로 생성됩니다.
  - 주문은 생성 후 10분간 유효합니다.

### 결제 주문 만료

- 유효시간이 지난 `READY` 주문은 `EXPIRED`로 변경됩니다.
- 연결된 `PENDING_PAYMENT` 회원권은 `CANCELLED`로 변경됩니다.
- `APPROVING`, `PAID` 주문과 활성 회원권은 자동 취소하지 않습니다.
- 서버가 1분 간격으로 만료 주문을 정리하며, 회원이 새 주문을 만들 때도 본인의 만료 주문을 먼저 정리합니다.

PG 승인 연동은 별도 단계에서 추가합니다.
- `POST /api/attendances/check-in`
- `POST /api/attendances/check-out`
- `GET /api/dashboard/summary`
