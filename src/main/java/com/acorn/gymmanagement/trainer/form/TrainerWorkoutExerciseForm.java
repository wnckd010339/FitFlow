package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TrainerWorkoutExerciseForm {
    @NotBlank(message = "운동 종목을 입력해 주세요.")
    private String exerciseName;
    @NotNull(message = "세트 수를 입력해 주세요.")
    @Min(value = 1, message = "세트 수는 1 이상이어야 합니다.")
    @Max(value = 20, message = "세트 수는 20 이하여야 합니다.")
    private Integer sets;
    @Min(value = 0, message = "중량은 0 이상이어야 합니다.")
    private BigDecimal weight;
    @Min(value = 0, message = "반복 횟수는 0 이상이어야 합니다.")
    @Max(value = 1000, message = "반복 횟수는 1000 이하여야 합니다.")
    private Integer reps;

    public TrainerWorkoutExerciseForm() { }

    public TrainerWorkoutExerciseForm(String exerciseName, Integer sets, BigDecimal weight, Integer reps) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.weight = weight;
        this.reps = reps;
    }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
}
