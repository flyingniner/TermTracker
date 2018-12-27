package com.termtacker.models;

import com.termtacker.utilities.Converters;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "TERMS")
public class Term
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TERM_ID")
    private int termId;

    @ColumnInfo(name="TERM_START_DATE")
    @TypeConverters(Converters.class)
    private LocalDate startDate;

    @ColumnInfo(name="TERM_END_DATE")
    @TypeConverters(Converters.class)
    private LocalDate endDate;

    @ColumnInfo(name="TERM_NAME")
    private String title;

    @ColumnInfo(name="STATUS")
    private String status;

    public Term(){}

    @Ignore
    public Term(String title, LocalDate startDate, LocalDate endDate, String status)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.status = status;

    }
    @Ignore
    public Term(int termId, String title, LocalDate startDate, LocalDate endDate, String status)
    {
        this.termId = termId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.status = status;
    }


//region getters and setters

    public int getTermId()
    {
        return termId;
    }

    public void setTermId(int termId)
    {
        this.termId = termId;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

//endregion



}
