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
- `POST /api/attendances/check-in`
- `POST /api/attendances/check-out`
- `GET /api/dashboard/summary`
