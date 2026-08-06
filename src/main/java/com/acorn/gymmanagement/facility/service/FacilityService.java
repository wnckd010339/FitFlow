package com.acorn.gymmanagement.facility.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.common.exception.ErrorCode;
import com.acorn.gymmanagement.facility.dto.request.EquipmentRegistrationRequest;
import com.acorn.gymmanagement.facility.dto.request.EquipmentSearchCondition;
import com.acorn.gymmanagement.facility.dto.response.EquipmentListResponse;
import com.acorn.gymmanagement.facility.dto.response.FacilitySummaryResponse;
import com.acorn.gymmanagement.facility.mapper.FacilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityService {
    private final FacilityMapper facilityMapper;

    public FacilitySummaryResponse getSummary() { return facilityMapper.findSummary(); }
    public List<EquipmentListResponse> findEquipment(EquipmentSearchCondition condition) { return facilityMapper.findEquipment(condition); }

    @Transactional
    public void register(EquipmentRegistrationRequest request) {
        if (facilityMapper.insertEquipment(request) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "기구를 등록하지 못했습니다.");
        }
    }
}
