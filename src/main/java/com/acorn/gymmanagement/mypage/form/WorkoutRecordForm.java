package com.acorn.gymmanagement.mypage.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRecordForm {
    private Long routineId;
    @NotNull(message = "운동 시간을 입력해 주세요.") @Min(value = 1, message = "운동 시간은 1분 이상이어야 합니다.") @Max(value = 1440, message = "운동 시간은 24시간 이하여야 합니다.")
    private Integer durationMinutes;
    @Size(max = 1000, message = "메모는 1000자 이하로 입력해 주세요.")
    private String memo;
    @Valid @Size(min = 1, message = "운동 항목을 하나 이상 입력해 주세요.")
    private List<WorkoutExerciseForm> exercises = new ArrayList<>();
    public WorkoutRecordForm() {}
    public WorkoutRecordForm(Long routineId,Integer durationMinutes,String memo,List<WorkoutExerciseForm> exercises){this.routineId=routineId;this.durationMinutes=durationMinutes;this.memo=memo;this.exercises=exercises==null?new ArrayList<>():new ArrayList<>(exercises);}
    public Long getRoutineId(){return routineId;} public void setRoutineId(Long v){routineId=v;}
    public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer v){durationMinutes=v;}
    public String getMemo(){return memo;} public void setMemo(String v){memo=v;}
    public List<WorkoutExerciseForm> getExercises(){return exercises;} public void setExercises(List<WorkoutExerciseForm> v){exercises=v;}
}
