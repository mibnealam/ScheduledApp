package com.example.mibne.scheduledapp.Models;

public class Routine {

    /* Course code of the routine */
    private String mCourseCode;

    /* Class starting time of the routines specific course */
    private String mStartTime;

    /* Class ending time of the routines specific course */
    private String mEndTime;

    /* Class holding day of the routines specific course */
    private String mDay;

    /* Room number of the routines specific course */
    private String mRoomNo;

    /* Rremarks of the routines specific course */
    private String mRemarks;

    public Routine(){
    }

    /**Constructs a new {@link Routine} object.
     *
     * @param day is the class holding day of the routines specific course
     * @param courseCode is the course code of the routine
     * @param startTime is the class starting time of the routines specific course
     * @param endTime is the class ending time of the routines specific course
     * @param roomNo is the room number of the routines specific course
     * @param remarks is the remarks of the routines specific course
     */
    public Routine (String day, String courseCode, String startTime, String endTime,
                   String roomNo, String remarks){
        this.mDay = day;
        this.mCourseCode = courseCode;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mRoomNo = roomNo;
        this.mRemarks = remarks;
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
    public String getStartTime (){
        return mStartTime;
    }
    public void setStartTime (String startTime){
        this.mStartTime = startTime;
    }
    /**
     * Returns the class ending time of a course of the routine.
     */
    public String getEndTime (){
        return mEndTime;
    }
    public void setEndTime (String endTime){
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

    /**
     * Returns the remarks of the routines specific routine
     */
    public String getRemarks() {
        return mRemarks;
    }

    public void setRemarks(String remarks) {
        this.mRemarks = remarks;
    }
}
