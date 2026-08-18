package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;

public class TrainerRoutineExerciseForm {
    @NotBlank(message="운동 종목을 입력해 주세요.") private String exerciseName;
    @NotNull(message="목표 세트를 입력해 주세요.") @Min(value=1,message="목표 세트는 1 이상이어야 합니다.") @Max(value=20,message="목표 세트는 20 이하여야 합니다.") private Integer targetSets;
    @Min(value=0,message="최소 반복 횟수는 0 이상이어야 합니다.") private Integer targetRepsMin;
    @Min(value=0,message="최대 반복 횟수는 0 이상이어야 합니다.") private Integer targetRepsMax;
    @Min(value=0,message="목표 중량은 0 이상이어야 합니다.") private BigDecimal targetWeight;
    @Min(value=0,message="휴식 시간은 0 이상이어야 합니다.") @Max(value=3600,message="휴식 시간은 3600초 이하여야 합니다.") private Integer restSeconds;
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
    @AssertTrue(message="최대 반복 횟수는 최소 반복 횟수 이상이어야 합니다.")
    public boolean isRepetitionRangeValid(){return targetRepsMin==null||targetRepsMax==null||targetRepsMax>=targetRepsMin;}
}
