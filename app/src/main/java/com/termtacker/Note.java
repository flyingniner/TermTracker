package com.termtacker;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "NOTES", foreignKeys = @ForeignKey(
        entity = Course.class,
        parentColumns = "COURSE_ID",
        childColumns = "FK_COURSE_ID"))
class Note
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "COURSE_NOTE_ID")
    private int courseNoteId;

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

//endregion

}
