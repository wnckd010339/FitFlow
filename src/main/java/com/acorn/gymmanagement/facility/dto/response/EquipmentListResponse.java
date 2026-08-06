package com.acorn.gymmanagement.facility.dto.response;

import java.time.LocalDate;

public record EquipmentListResponse(Long equipmentId, String managementCode, String name, String category,
                                    String location, String status, LocalDate lastMaintenanceDate,
                                    LocalDate nextDueDate) { }
