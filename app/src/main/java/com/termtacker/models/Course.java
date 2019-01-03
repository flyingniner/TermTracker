package com.termtacker.models;

import android.graphics.drawable.Drawable;

import com.termtacker.utilities.Converters;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

//@Entity(tableName = "COURSES"), foreignKeys = {
//        @ForeignKey(
//                entity = Term.class,
//                parentColumns = "TERM_ID",
//                childColumns = "FK_TERM_ID"),
//        @ForeignKey(
//                entity = Mentor.class,
//                parentColumns = "MENTOR_ID",
//                childColumns = "FK_MENTOR_ID"
//        )})

@Entity(tableName = "COURSES")
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


    @ColumnInfo(name = "COURSE_END_DATE")
    @TypeConverters(Converters.class)
    private LocalDate endDate;

    @ColumnInfo(name = "COURSE_STATUS")
    private String status;

    @ColumnInfo(name = "COURSE_NOTES")
    private String notes;

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
    public Course(String title, LocalDate startDate, LocalDate endDate, String status,int courseMentorId, int termId)
    {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseMentorId = courseMentorId;
        this.termId = termId;
        this.status = status;
    }

    @Ignore
    public Course(int courseId, String title, LocalDate startDate, LocalDate endDate, String status,int courseMentorId, int termId)
    {
        this.courseId = courseId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
//        this.goalDate = goalDate;
        this.courseMentorId = courseMentorId;
        this.termId = termId;
        this.status = status;
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

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) {this.status = status;}

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

