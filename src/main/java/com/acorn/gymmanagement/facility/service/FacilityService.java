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
import java.time.LocalDateTime;
import com.acorn.gymmanagement.facility.dto.request.MaintenanceRegistrationRequest;
import com.acorn.gymmanagement.facility.model.MaintenanceRegistration;

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

    @Transactional
    public void recordMaintenance(Long equipmentId, MaintenanceRegistrationRequest request, Long adminUserId) {
        if (!facilityMapper.existsEquipment(equipmentId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "기구를 찾을 수 없습니다.");
        }
        MaintenanceRegistration registration = new MaintenanceRegistration(equipmentId, request.maintenanceType(),
                request.description().trim(), LocalDateTime.now(), adminUserId, request.nextDueDate());
        if (facilityMapper.insertMaintenanceLog(registration) != 1
                || facilityMapper.updateStatus(equipmentId, request.resultingStatus()) != 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "시설 점검 이력 저장에 실패했습니다.");
        }
    }
}
