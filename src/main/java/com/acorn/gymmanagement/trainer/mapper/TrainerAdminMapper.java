package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.AssignedMemberResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import com.acorn.gymmanagement.trainer.model.TrainerAssignmentRegistration;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainerAdminMapper {
    TrainerSummaryResponse findSummary();
    List<TrainerListResponse> findTrainers(TrainerSearchCondition condition);
    List<WaitingMemberResponse> findWaitingMembers();
    List<AssignedMemberResponse> findAssignedMembers();
    boolean existsActiveMember(Long memberId);
    boolean existsActiveTrainer(Long trainerId);
    boolean existsActiveAssignment(Long memberId);
    int insertAssignment(TrainerAssignmentRegistration registration);
    int endActiveAssignment(@Param("memberId") Long memberId, @Param("endedAt") LocalDate endedAt);
}
