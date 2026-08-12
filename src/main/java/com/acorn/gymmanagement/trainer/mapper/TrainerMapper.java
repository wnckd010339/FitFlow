package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.AssignedMemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.acorn.gymmanagement.trainer.model.TrainerAssignmentRegistration;

import java.util.List;

@Mapper
public interface TrainerMapper {
    TrainerSummaryResponse findSummary();
    List<TrainerListResponse> findTrainers(TrainerSearchCondition condition);
    List<WaitingMemberResponse> findWaitingMembers();
    List<AssignedMemberResponse> findAssignedMembers();
    boolean existsWaitingMember(Long memberId);
    boolean existsActiveMember(Long memberId);
    boolean existsActiveTrainer(Long trainerId);
    boolean existsActiveAssignment(Long memberId);
    int insertAssignment(TrainerAssignmentRegistration registration);
    int endActiveAssignment(@Param("memberId") Long memberId, @Param("endedAt") java.time.LocalDate endedAt);
}
