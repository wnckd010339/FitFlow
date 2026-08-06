# payment Mapper

해당 도메인의 MyBatis XML Mapper를 배치합니다.
# Payment Mapper

`PaymentMapper.xml`은 회원권 현장 결제와 환불에 필요한 SQL을 관리합니다.

- 결제 대상 회원권 잠금 조회
- 회원권 중복 결제 확인
- 결제 저장 및 단건 조회
- 전체 또는 회원별 결제·환불 이력 조회
- 환불 저장과 결제 상태 변경

결제 완료와 전액 환불처럼 회원권 상태도 함께 변경하는 업무는
`PaymentService`의 트랜잭션 안에서 `MembershipMapper`와 조합합니다.
