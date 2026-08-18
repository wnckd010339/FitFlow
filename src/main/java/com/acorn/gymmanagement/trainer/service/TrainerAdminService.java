package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.trainer.dto.request.AssignTrainerRequest;
import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.AssignedMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import com.acorn.gymmanagement.trainer.mapper.TrainerAdminMapper;
import com.acorn.gymmanagement.trainer.model.TrainerAssignmentRegistration;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerAdminService {
    private final TrainerAdminMapper trainerAdminMapper;

    public TrainerSummaryResponse getSummary() { return trainerAdminMapper.findSummary(); }
    public List<TrainerListResponse> findTrainers(TrainerSearchCondition condition) {
        return trainerAdminMapper.findTrainers(condition);
    }
    public List<WaitingMemberResponse> findWaitingMembers() {
        return trainerAdminMapper.findWaitingMembers();
    }
    public List<AssignedMemberResponse> findAssignedMembers() {
        return trainerAdminMapper.findAssignedMembers();
    }

    @Transactional
    public void assign(AssignTrainerRequest request, Long adminUserId) {
        if (!trainerAdminMapper.existsActiveMember(request.memberId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활성 회원을 찾을 수 없습니다.");
        }
        if (!trainerAdminMapper.existsActiveTrainer(request.trainerId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활동 중인 트레이너를 찾을 수 없습니다.");
        }
        if (trainerAdminMapper.existsActiveAssignment(request.memberId())
                && trainerAdminMapper.endActiveAssignment(request.memberId(), request.startedAt()) != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "기존 트레이너 배정을 종료하지 못했습니다.");
        }
        var registration = new TrainerAssignmentRegistration(
                request.memberId(), request.trainerId(), request.startedAt(), adminUserId);
        if (trainerAdminMapper.insertAssignment(registration) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "트레이너 배정에 실패했습니다.");
        }
    }

    @Transactional
    public void unassign(Long memberId, LocalDate endedAt) {
        if (trainerAdminMapper.endActiveAssignment(memberId, endedAt) != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "활성 트레이너 배정을 찾을 수 없습니다.");
        }
    }
}
