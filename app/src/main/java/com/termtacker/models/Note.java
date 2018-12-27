package com.termtacker.models;

import com.termtacker.models.Course;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "NOTES", foreignKeys = @ForeignKey(
        entity = Course.class,
        parentColumns = "COURSE_ID",
        childColumns = "FK_COURSE_ID"))
public class Note
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "COURSE_NOTE_ID")
    private int courseNoteId;

    @ColumnInfo(name = "NOTE_DATE")
    private LocalDate noteDate;

    @ColumnInfo(name = "NOTE")
    private String note;

    @ColumnInfo(name = "FK_COURSE_ID", index = true)
    private int courseId;

//region getters and setters

    public int getCourseNoteId()
    {
        return courseNoteId;
    }

    public void setCourseNoteId(int courseNoteId)
    {
        this.courseNoteId = courseNoteId;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public int getCourseId()
    {
        return courseId;
    }

    public void setCourseId(int courseId)
    {
        this.courseId = courseId;
    }

    public LocalDate getNoteDate()
    {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate)
    {
        this.noteDate = noteDate;
    }

//endregion

}
