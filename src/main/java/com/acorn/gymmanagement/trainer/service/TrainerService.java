package com.acorn.gymmanagement.trainer.service;

import com.acorn.gymmanagement.trainer.dto.request.TrainerSearchCondition;
import com.acorn.gymmanagement.trainer.dto.response.TrainerListResponse;
import com.acorn.gymmanagement.trainer.dto.response.TrainerSummaryResponse;
import com.acorn.gymmanagement.trainer.dto.response.WaitingMemberResponse;
import com.acorn.gymmanagement.trainer.mapper.TrainerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
