package com.example.mibne.scheduledapp;

public class Notice {
    /* Title of the notice */
    private String mNoticeTitle;

    /* Creation time of the notice */
    private Long mNoticeDate;

    /* Deadline of the notice */
    private Long mNoticeDeadline;

    /* Description/body of the notice */
    private String mNoticeDescription;

    /* Type of the notice */
    private String mNoticeType;

    /* Creator of the notice */
    private String mNoticeOwner;

    /* Priority value of the notice */
    private String mNoticePriority;

    public Notice(){
    }

    /**Constructs a new {@link Course} object.
     *
     * @param noticeTitle is the title of the notice
     * @param noticeDate is the creation time of the notice
     * @param noticeDeadline is the deadline time of the notice
     * @param noticeDescription is the body of the notice
     * @param noticeType is the type of the notice which can be either notice or assignment
     * @param noticeOwner is the owner id of the notice
     * @param noticePriority is the priority value of the notice high/medium/low
     */
    public Notice (String noticeTitle, Long noticeDate, Long noticeDeadline,
                   String noticeDescription, String noticeType, String noticeOwner,
                   String noticePriority){
        this.mNoticeTitle = noticeTitle;
        this.mNoticeDate = noticeDate;
        this.mNoticeDeadline = noticeDeadline;
        this.mNoticeDescription = noticeDescription;
        this.mNoticeType = noticeType;
        this.mNoticeOwner = noticeOwner;
        this.mNoticePriority = noticePriority;
    }

    /**
     * Returns the Title of the notice.
     */
    public String getNoticeTitle (){
        return mNoticeTitle;
    }
    public void setNoticeTitle (String noticeTitle){
        this.mNoticeTitle = noticeTitle;
    }
    /**
     * Returns the creation time of the notice.
     */
    public Long getNoticeDate (){
        return mNoticeDate;
    }
    public void setNoticeDate (Long noticeDate){
        this.mNoticeDate = noticeDate;
    }
    /**
     * Returns the deadline of the notice.
     */
    public Long getNoticeDeadline (){
        return mNoticeDeadline;
    }
    public void setNoticeDeadline (Long noticeDeadline){
        this.mNoticeDeadline = noticeDeadline;
    }
    /**
     * Returns the description/body of the notice.
     */
    public String getNoticeDescription (){
        return mNoticeDescription;
    }
    public void setNoticeDescription (String description){
        this.mNoticeDescription = description;
    }
    /**
     * Returns the type of the notice.
     */
    public String getNoticeType (){
        return mNoticeType;
    }
    public void setNoticeType (String noticeType){
        this.mNoticeType = noticeType;
    }
    /**
     * Returns the Owner id of the notice.
     */
    public String getNoticeOwner (){
        return mNoticeOwner;
    }
    public void setNoticeOwner (String noticeOwner){
        this.mNoticeOwner = noticeOwner;
    }
    /**
     * Returns the priority of the notice.
     */
    public String getNoticePriority (){
        return mNoticePriority;
    }
    public void setNoticePriority (String noticePriority){
        this.mNoticePriority = noticePriority;
    }
}
