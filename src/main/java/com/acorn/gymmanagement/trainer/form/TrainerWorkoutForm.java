package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class TrainerWorkoutForm {
    @NotNull(message = "회원을 선택해 주세요.")
    private Long memberId;
    private Long routineId;
    @NotNull(message = "운동 시간을 입력해 주세요.")
    @Min(value = 1, message = "운동 시간은 1분 이상이어야 합니다.")
    @Max(value = 1440, message = "운동 시간은 1440분 이하여야 합니다.")
    private Integer durationMinutes;
    private String memo;
    @Valid
    @Size(min = 1, message = "운동 항목을 하나 이상 입력해 주세요.")
    private List<TrainerWorkoutExerciseForm> exercises = new ArrayList<>();

    public TrainerWorkoutForm() { }

    public TrainerWorkoutForm(Long memberId, Long routineId, Integer durationMinutes,
                              String memo, List<TrainerWorkoutExerciseForm> exercises) {
        this.memberId = memberId;
        this.routineId = routineId;
        this.durationMinutes = durationMinutes;
        this.memo = memo;
        this.exercises = new ArrayList<>(exercises);
    }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getRoutineId() { return routineId; }
    public void setRoutineId(Long routineId) { this.routineId = routineId; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public List<TrainerWorkoutExerciseForm> getExercises() { return exercises; }
    public void setExercises(List<TrainerWorkoutExerciseForm> exercises) { this.exercises = exercises; }
}
