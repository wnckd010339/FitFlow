package com.acorn.gymmanagement.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record EquipmentUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Pattern(regexp = "CHEST|BACK|SHOULDER|LEG|ARM|CARDIO|FREE_WEIGHT|MULTI") String category,
        @NotBlank @Size(max = 100) String location,
        @NotBlank @Pattern(regexp = "AVAILABLE|INSPECTION|REPAIR|UNAVAILABLE") String status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate purchasedAt
) { }
