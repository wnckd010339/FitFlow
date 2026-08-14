package com.acorn.gymmanagement.trainer.model;

import java.time.LocalDate;

public class TrainerRoutineRegistration {
    private Long routineId;
    private final Long memberId;
    private final Long trainerId;
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public TrainerRoutineRegistration(Long memberId, Long trainerId, String title, String description, LocalDate startDate, LocalDate endDate) {
        this.memberId = memberId; this.trainerId = trainerId; this.title = title; this.description = description; this.startDate = startDate; this.endDate = endDate;
    }
    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }
    public Long getMemberId() { return memberId; }
    public Long getTrainerId() { return trainerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
