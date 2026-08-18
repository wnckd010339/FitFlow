package com.acorn.gymmanagement.trainer.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrainerRoutineForm {
    @NotNull(message = "회원을 선택해 주세요.") private Long memberId;
    @NotBlank(message = "루틴 제목을 입력해 주세요.") private String title;
    private String description;
    @NotNull(message = "시작일을 입력해 주세요.") private LocalDate startDate;
    private LocalDate endDate;
    @NotBlank(message = "운동 구성명을 입력해 주세요.") private String workoutGroupTitle;
    private Integer weekNumber;
    private Integer dayOfWeek;
    @Valid @NotEmpty(message = "운동을 한 개 이상 추가해 주세요.")
    private List<TrainerRoutineExerciseForm> exercises = new ArrayList<>();

    public TrainerRoutineForm() { }
    public TrainerRoutineForm(Long memberId, String title, String description, LocalDate startDate,
                              LocalDate endDate, String workoutGroupTitle, Integer weekNumber,
                              Integer dayOfWeek, List<TrainerRoutineExerciseForm> exercises) {
        this.memberId=memberId; this.title=title; this.description=description; this.startDate=startDate;
        this.endDate=endDate; this.workoutGroupTitle=workoutGroupTitle; this.weekNumber=weekNumber;
        this.dayOfWeek=dayOfWeek; this.exercises=exercises==null?new ArrayList<>():new ArrayList<>(exercises);
    }
    public Long getMemberId(){return memberId;} public void setMemberId(Long v){memberId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
    public String getWorkoutGroupTitle(){return workoutGroupTitle;} public void setWorkoutGroupTitle(String v){workoutGroupTitle=v;}
    public Integer getWeekNumber(){return weekNumber;} public void setWeekNumber(Integer v){weekNumber=v;}
    public Integer getDayOfWeek(){return dayOfWeek;} public void setDayOfWeek(Integer v){dayOfWeek=v;}
    public List<TrainerRoutineExerciseForm> getExercises(){return exercises;} public void setExercises(List<TrainerRoutineExerciseForm> v){exercises=v;}
    @AssertTrue(message = "종료일은 시작일보다 빠를 수 없습니다.")
    public boolean isPeriodValid(){return startDate==null||endDate==null||!endDate.isBefore(startDate);}
}
