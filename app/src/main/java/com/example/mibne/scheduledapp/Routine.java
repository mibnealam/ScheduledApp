package com.example.mibne.scheduledapp;

public class Routine {

    /* Course code of the routine */
    private String mCourseCode;

    /* Class starting time of the routines specific course */
    private Long mStartTime;

    /* Class ending time of the routines specific course */
    private Long mEndTime;

    /* Class holding day of the routines specific course */
    private String mDay;

    /* Room number of the routines specific course */
    private String mRoomNo;

    public Routine(){
    }

    /**Constructs a new {@link Routine} object.
     *
     * @param courseCode is the course code of the routine
     * @param startTime is the class starting time of the routines specific course
     * @param endTime is the class ending time of the routines specific course
     * @param day is the class holding day of the routines specific course
     * @param roomNo is the room number of the routines specific course
     */
    public Routine (String courseCode, Long startTime, Long endTime,
                   String day, String roomNo){
        this.mCourseCode = courseCode;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mDay = day;
        this.mRoomNo = roomNo;
    }

    /**
     * Returns the course code of the routine.
     */
    public String getCourseCode (){
        return mCourseCode;
    }
    public void setCourseCode (String courseId){
        this.mCourseCode = courseId;
    }
    /**
     * Returns the class starting time of a course of the routine.
     */
    public Long getStartTime (){
        return mStartTime;
    }
    public void setStartTime (Long startTime){
        this.mStartTime = startTime;
    }
    /**
     * Returns the class ending time of a course of the routine.
     */
    public Long getEndTime (){
        return mEndTime;
    }
    public void setEndTime (Long endTime){
        this.mEndTime = endTime;
    }
    /**
     * Returns the class holding day of the routines specific course
     */
    public String getDay (){
        return mDay;
    }
    public void setDay (String day){
        this.mDay = day;
    }
    /**
     * Returns the room number of the routines specific course
     */
    public String getRoomNo (){
        return mRoomNo;
    }
    public void setRoomNo (String roomNo){
        this.mRoomNo = roomNo;
    }

}
