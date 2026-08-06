package com.acorn.gymmanagement.trainer.mapper;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TrainerMapper {
    TrainerSummaryResponse findSummary();
    List<TrainerListResponse> findTrainers(TrainerSearchCondition condition);
    List<WaitingMemberResponse> findWaitingMembers();
}
