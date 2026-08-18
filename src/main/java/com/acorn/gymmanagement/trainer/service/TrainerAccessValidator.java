package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.mapper.TrainerMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerAccessValidator {
    private final TrainerMemberMapper trainerMemberMapper;

    public Long requireTrainerId(Long userId) {
        return trainerMemberMapper.findTrainerId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND, "트레이너 정보를 찾을 수 없습니다."));
    }

    public void requireAssignedMember(Long userId, Long memberId) {
        requireAssignedMemberByTrainerId(requireTrainerId(userId), memberId);
    }

    public void requireAssignedMemberByTrainerId(Long trainerId, Long memberId) {
        if (!trainerMemberMapper.existsAssignedMember(trainerId, memberId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "담당 회원만 조회하거나 수정할 수 있습니다.");
        }
    }
}
