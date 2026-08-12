package com.acorn.gymmanagement.facility.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record MaintenanceRegistration(Long equipmentId, String maintenanceType, String description,
                                      LocalDateTime performedAt, Long performedBy, LocalDate nextDueDate) { }
