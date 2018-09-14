package com.example.mibne.scheduledapp;

public class Course {

    /* Name of the course */
    private String mCourseName;

    /* Course code of the course */
    private String mCourseCode;

    /* Credit hours of the course */
    private double mCredit;

    /**Constructs a new {@link Course} object.
     *
     * @param credit is the credit hours of the course
     * @param courseCode is the course code of the curse
     * @param courseName is the course name of the course
     */
    public Course (double credit, String courseCode, String courseName){

        mCredit = credit;
        mCourseCode = courseCode;
        mCourseName = courseName;
    }

    /**
     * Returns the credit hours of the course.
     */
    public double getCredit (){
        return mCredit;
    }
    /**
     * Returns the course code of the course.
     */
    public String getCourseCode (){
        return mCourseCode;
    }
    /**
     * Returns the name of the course.
     */
    public String getCourseName (){
        return mCourseName;
    }
}
