package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import com.acorn.gymmanagement.trainer.mapper.TrainerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.dto.request.AssignTrainerRequest;
import com.acorn.gymmanagement.trainer.model.TrainerAssignmentRegistration;
import com.acorn.gymmanagement.trainer.dto.response.AssignedMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerHomeMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerHomeProfileResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {
    private final TrainerMapper trainerMapper;

    public TrainerSummaryResponse getSummary() { return trainerMapper.findSummary(); }
    public List<TrainerListResponse> findTrainers(TrainerSearchCondition condition) { return trainerMapper.findTrainers(condition); }
    public List<WaitingMemberResponse> findWaitingMembers() { return trainerMapper.findWaitingMembers(); }
    public List<AssignedMemberResponse> findAssignedMembers() { return trainerMapper.findAssignedMembers(); }

    public TrainerHomeProfileResponse getHomeProfile(Long userId) {
        TrainerHomeProfileResponse profile = trainerMapper.findHomeProfile(userId);
        if (profile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "트레이너 정보를 찾을 수 없습니다.");
        }
        return profile;
    }

    public List<TrainerHomeMemberResponse> findHomeMembers(Long userId) {
        return trainerMapper.findHomeMembers(userId);
    }

    @Transactional
    public void assign(AssignTrainerRequest request, Long adminUserId) {
        if (!trainerMapper.existsActiveMember(request.memberId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활성 회원을 찾을 수 없습니다.");
        }
        if (!trainerMapper.existsActiveTrainer(request.trainerId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활동 중인 트레이너를 찾을 수 없습니다.");
        }
        if (trainerMapper.existsActiveAssignment(request.memberId())) {
            if (trainerMapper.endActiveAssignment(request.memberId(), request.startedAt()) != 1) {
                throw new BusinessException(ErrorCode.CONFLICT, "기존 트레이너 배정을 종료하지 못했습니다.");
            }
        }
        TrainerAssignmentRegistration registration = new TrainerAssignmentRegistration(
                request.memberId(), request.trainerId(), request.startedAt(), adminUserId);
        if (trainerMapper.insertAssignment(registration) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "트레이너 배정에 실패했습니다.");
        }
    }

    @Transactional
    public void unassign(Long memberId, java.time.LocalDate endedAt) {
        if (trainerMapper.endActiveAssignment(memberId, endedAt) != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활성 트레이너 배정을 찾을 수 없습니다.");
        }
    }
}
