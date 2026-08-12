package com.acorn.gymmanagement.facility.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
public record MaintenanceRegistrationRequest(
        @NotBlank @Pattern(regexp="CLEANING|INSPECTION|REPAIR") String maintenanceType,
        @NotBlank @Size(max=1000) String description,
        @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate nextDueDate,
        @NotBlank @Pattern(regexp="AVAILABLE|INSPECTION|REPAIR|UNAVAILABLE") String resultingStatus) { }
