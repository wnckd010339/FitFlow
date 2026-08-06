package com.acorn.gymmanagement.dashboard.dto.response;

import java.time.LocalDateTime;

public record DashboardActivityResponse(String type, String title, String description, LocalDateTime occurredAt) { }
