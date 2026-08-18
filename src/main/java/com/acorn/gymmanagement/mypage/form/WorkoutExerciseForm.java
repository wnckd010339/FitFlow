package com.acorn.gymmanagement.mypage.form;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class WorkoutExerciseForm {
    @NotBlank(message="운동 종목을 입력해 주세요.") private String exerciseName;
    @NotNull(message="세트 수를 입력해 주세요.") @Min(value=1,message="세트 수는 1 이상이어야 합니다.") @Max(value=20,message="세트 수는 20 이하여야 합니다.") private Integer sets;
    @Min(value=0,message="중량은 0 이상이어야 합니다.") private BigDecimal weight;
    @Min(value=0,message="횟수는 0 이상이어야 합니다.") @Max(value=1000,message="횟수는 1000 이하여야 합니다.") private Integer reps;
    public WorkoutExerciseForm(){} public WorkoutExerciseForm(String n,Integer s,BigDecimal w,Integer r){exerciseName=n;sets=s;weight=w;reps=r;}
    public String getExerciseName(){return exerciseName;} public void setExerciseName(String v){exerciseName=v;}
    public Integer getSets(){return sets;} public void setSets(Integer v){sets=v;}
    public BigDecimal getWeight(){return weight;} public void setWeight(BigDecimal v){weight=v;}
    public Integer getReps(){return reps;} public void setReps(Integer v){reps=v;}
}
