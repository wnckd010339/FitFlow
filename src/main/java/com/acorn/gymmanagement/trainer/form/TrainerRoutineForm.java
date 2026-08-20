package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class TrainerRoutineForm {
    @NotNull(message = "회원을 선택해 주세요.")
    private Long memberId;
    @NotBlank(message = "루틴 제목을 입력해 주세요.")
    private String title;
    private String description;
    @NotNull(message = "시작일을 입력해 주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    @Valid
    @NotEmpty(message = "운동을 한 개 이상 추가해 주세요.")
    private List<TrainerRoutineExerciseForm> exercises = new ArrayList<>();

    public TrainerRoutineForm() { }

    public TrainerRoutineForm(
            Long memberId, String title, String description, LocalDate startDate,
            LocalDate endDate, List<TrainerRoutineExerciseForm> exercises) {
        this.memberId = memberId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.exercises = exercises == null ? new ArrayList<>() : new ArrayList<>(exercises);
    }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public List<TrainerRoutineExerciseForm> getExercises() { return exercises; }
    public void setExercises(List<TrainerRoutineExerciseForm> exercises) { this.exercises = exercises; }

    @AssertTrue(message = "종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isPeriodValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
