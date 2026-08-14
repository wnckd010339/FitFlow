package com.acorn.gymmanagement.trainer.model;
public class TrainerRoutineWorkoutGroupRegistration {
    private Long workoutGroupId;
    private final Long routineId; private final String title; private final Integer weekNumber;
    private final Integer dayOfWeek; private final Integer displayOrder;
    public TrainerRoutineWorkoutGroupRegistration(Long routineId,String title,Integer weekNumber,Integer dayOfWeek,Integer displayOrder){this.routineId=routineId;this.title=title;this.weekNumber=weekNumber;this.dayOfWeek=dayOfWeek;this.displayOrder=displayOrder;}
    public Long getWorkoutGroupId(){return workoutGroupId;} public void setWorkoutGroupId(Long v){workoutGroupId=v;}
    public Long getRoutineId(){return routineId;} public String getTitle(){return title;} public Integer getWeekNumber(){return weekNumber;} public Integer getDayOfWeek(){return dayOfWeek;} public Integer getDisplayOrder(){return displayOrder;}
}
