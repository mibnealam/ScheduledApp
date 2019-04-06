package com.example.mibne.scheduledapp.Models;

public class Course {

    /* Name of the course */
    private String mCourseName;

    /* Course code of the course */
    private String mCourseCode;

    /* Credit hours of the course */
    private String mCourseCredit;

    private boolean isSelected;

    public Course(){
    }

    /**Constructs a new {@link Course} object.
     *
     * @param courseCredit is the credit hours of the course
     * @param courseCode is the course code of the curse
     * @param courseName is the course name of the course
     */
    public Course (String courseCredit, String courseCode, String courseName){
        this.mCourseCredit = courseCredit;
        this.mCourseCode = courseCode;
        this.mCourseName = courseName;
    }
    /**
     * Returns the course credit of the course.
     */
    public String getCourseCredit (){
        return mCourseCredit;
    }
    public void setCourseCredit (String courseCredit){
        this.mCourseCredit = courseCredit;
    }

    /**
     * Returns the course code of the course.
     */
    public String getCourseCode (){
        return mCourseCode;
    }
    public void setCourseCode (String courseCode){
        this.mCourseCode = courseCode;
    }
    /**
     * Returns the name of the course.
     */
    public String getCourseName (){
        return mCourseName;
    }
    public void setCourseName (String courseName){
        this.mCourseName = courseName;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
