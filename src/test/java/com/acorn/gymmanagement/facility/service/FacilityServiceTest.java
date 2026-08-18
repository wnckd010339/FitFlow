package com.acorn.gymmanagement.facility.service;

import com.acorn.gymmanagement.common.exception.BusinessException;
import com.acorn.gymmanagement.facility.dto.request.EquipmentUpdateRequest;
import com.acorn.gymmanagement.facility.mapper.FacilityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {
    @Mock private FacilityMapper facilityMapper;
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        facilityService = new FacilityService(facilityMapper);
    }

    @Test
    void updatesExistingEquipment() {
        EquipmentUpdateRequest request = request();
        when(facilityMapper.existsEquipment(1L)).thenReturn(true);
        when(facilityMapper.updateEquipment(1L, request)).thenReturn(1);

        facilityService.update(1L, request);

        verify(facilityMapper).updateEquipment(1L, request);
    }

    @Test
    void rejectsUnknownEquipment() {
        EquipmentUpdateRequest request = request();
        when(facilityMapper.existsEquipment(1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> facilityService.update(1L, request));
        verify(facilityMapper, never()).updateEquipment(1L, request);
    }

    private EquipmentUpdateRequest request() {
        return new EquipmentUpdateRequest("체스트 프레스", "CHEST", "1층", "AVAILABLE", LocalDate.of(2026, 1, 1));
    }
}
