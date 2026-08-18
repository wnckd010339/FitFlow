# FitFlow 데이터베이스

## 신규 환경

아래 순서로 SQL을 실행합니다.

1. `schema.sql`: 기존 FitFlow 테이블을 제거하고 전체 스키마를 생성합니다.
2. `sample-data.sql`: 로컬 개발과 시연에 사용하는 샘플 데이터를 입력합니다.

## 기존 환경 업데이트

기존 데이터를 유지해야 할 때는 `schema.sql`을 실행하지 않고,
`migrations` 디렉터리의 파일을 번호 순서대로 실행합니다.

- `001-add-routine-workout-groups.sql`: 루틴 운동 그룹 구조 추가
- `002-add-payment-orders.sql`: 결제 주문 구조 추가

각 마이그레이션은 선행 테이블이 이미 생성된 상태에서 실행해야 합니다.
