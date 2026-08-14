package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TrainerRoutineExerciseForm {
    @NotBlank(message="운동 종목을 입력해 주세요.") private String exerciseName;
    @NotNull(message="목표 세트를 입력해 주세요.") @Min(1) @Max(20) private Integer targetSets;
    @Min(0) private Integer targetRepsMin;
    @Min(0) private Integer targetRepsMax;
    @Min(0) private BigDecimal targetWeight;
    @Min(0) @Max(3600) private Integer restSeconds;
    private String memo;

    public TrainerRoutineExerciseForm() { }
    public TrainerRoutineExerciseForm(String exerciseName,Integer targetSets,Integer targetRepsMin,
            Integer targetRepsMax,BigDecimal targetWeight,Integer restSeconds,String memo){
        this.exerciseName=exerciseName;this.targetSets=targetSets;this.targetRepsMin=targetRepsMin;
        this.targetRepsMax=targetRepsMax;this.targetWeight=targetWeight;this.restSeconds=restSeconds;this.memo=memo;
    }
    public String getExerciseName(){return exerciseName;} public void setExerciseName(String v){exerciseName=v;}
    public Integer getTargetSets(){return targetSets;} public void setTargetSets(Integer v){targetSets=v;}
    public Integer getTargetRepsMin(){return targetRepsMin;} public void setTargetRepsMin(Integer v){targetRepsMin=v;}
    public Integer getTargetRepsMax(){return targetRepsMax;} public void setTargetRepsMax(Integer v){targetRepsMax=v;}
    public BigDecimal getTargetWeight(){return targetWeight;} public void setTargetWeight(BigDecimal v){targetWeight=v;}
    public Integer getRestSeconds(){return restSeconds;} public void setRestSeconds(Integer v){restSeconds=v;}
    public String getMemo(){return memo;} public void setMemo(String v){memo=v;}
}
