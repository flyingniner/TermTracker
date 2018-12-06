package com.termtacker;

import android.graphics.drawable.Drawable;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "COURSES", foreignKeys = {
        @ForeignKey(
                entity = Term.class,
                parentColumns = "TERM_ID",
                childColumns = "FK_TERM_ID"),
        @ForeignKey(
                entity = Mentor.class,
                parentColumns = "MENTOR_ID",
                childColumns = "FK_MENTOR_ID"
        )})
public class Course
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "COURSE_ID")
    private int courseId;

    @ColumnInfo(name = "COURSE_TITLE")
    private String title;

    @ColumnInfo(name = "COURSE_START_DATE")
    @TypeConverters(Converters.class)
    private LocalDate startDate;


    @ColumnInfo(name = "COURSE_COMPLETED_DATE")
    @TypeConverters(Converters.class)
    private LocalDate endDate;

    @ColumnInfo(name = "COURSE_GOAL_DATE")
    @TypeConverters(Converters.class)
    private LocalDate goalDate;

    @ColumnInfo(name = "FK_MENTOR_ID", index = true)
    private int courseMentorId;

    @ColumnInfo(name = "FK_TERM_ID", index = true)
    private int termId;

    //    @ColumnInfo(name = "COURSE_STATUS_ICON")
    @Ignore
    private Drawable statusIcon;

    public Course()
    {

    }

    @Ignore
    public Course(int courseId, String title, LocalDate startDate, LocalDate endDate, LocalDate goalDate, int courseMentorId, int termId)
    {
        this.courseId = courseId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goalDate = goalDate;
        this.courseMentorId = courseMentorId;
        this.termId = termId;
    }


//region getters and setters

    public int getCourseId()
    {
        return courseId;
    }

    public void setCourseId(int courseId)
    {
        this.courseId = courseId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }

    public LocalDate getGoalDate()
    {
        return goalDate;
    }

    public void setGoalDate(LocalDate goalDate)
    {
        this.goalDate = goalDate;
    }

    public int getCourseMentorId()
    {
        return courseMentorId;
    }

    public void setCourseMentorId(int courseMentorId)
    {
        this.courseMentorId = courseMentorId;
    }

    public int getTermId()
    {
        return termId;
    }

    public void setTermId(int termId)
    {
        this.termId = termId;
    }

    public Drawable getStatusIcon()
    {
        return statusIcon;
    }

    public void setStatusIcon(Drawable statusIcon)
    {
        this.statusIcon = statusIcon;
    }

//endregion

}
