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

회원 결제 생성 API는 PG 연동 단계에서 회원 본인 소유권 검증과 함께 추가합니다.
- `POST /api/attendances/check-in`
- `POST /api/attendances/check-out`
- `GET /api/dashboard/summary`
