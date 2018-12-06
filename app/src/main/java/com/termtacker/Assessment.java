package com.termtacker;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "ASSESSMENTS", foreignKeys = @ForeignKey(
        entity = Course.class,
        parentColumns = "COURSE_ID",
        childColumns = "FK_COURSE_ID"))
public class Assessment
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ASSESSMENT_ID")
    private int assessmentId;

    @ColumnInfo(name = "ASSESSMENT_CODE")
    private String assessmentCode;

    @ColumnInfo(name = "ASSESSMENT_TYPE")
    private String assessmentType;

    @ColumnInfo(name = "ASSESSMENT_DATE")
    @TypeConverters(Converters.class)
    private LocalDate assessmentDate;

    @ColumnInfo(name = "RESULT")
    private String result;

    @ColumnInfo(name = "FK_COURSE_ID", index = true)
    private int courseId;

//region getters & setters

    public int getAssessmentId()
    {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId)
    {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentCode()
    {
        return assessmentCode;
    }

    public void setAssessmentCode(String assessmentCode)
    {
        this.assessmentCode = assessmentCode;
    }

    public LocalDate getAssessmentDate()
    {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate)
    {
        this.assessmentDate = assessmentDate;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public int getCourseId()
    {
        return courseId;
    }

    public void setCourseId(int courseId)
    {
        this.courseId = courseId;
    }

    public String getAssessmentType()
    {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType)
    {
        this.assessmentType = assessmentType;
    }

//endregion

}
