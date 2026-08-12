package com.acorn.gymmanagement.facility.mapper;

import com.acorn.gymmanagement.facility.dto.request.EquipmentRegistrationRequest;
import com.acorn.gymmanagement.facility.dto.request.EquipmentSearchCondition;
import com.acorn.gymmanagement.facility.dto.response.EquipmentListResponse;
import com.acorn.gymmanagement.facility.dto.response.FacilitySummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.acorn.gymmanagement.facility.model.MaintenanceRegistration;

import java.util.List;

@Mapper
public interface FacilityMapper {
    FacilitySummaryResponse findSummary();
    List<EquipmentListResponse> findEquipment(EquipmentSearchCondition condition);
    int insertEquipment(EquipmentRegistrationRequest request);
    boolean existsEquipment(Long equipmentId);
    int insertMaintenanceLog(MaintenanceRegistration registration);
    int updateStatus(@Param("equipmentId") Long equipmentId, @Param("status") String status);
}
