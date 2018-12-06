package com.termtacker;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MENTORS")
public class Mentor
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "MENTOR_ID")
    private int mentorId;

    @ColumnInfo(name = "MENTOR_NAME")
    private String name;

    @ColumnInfo(name = "MENTOR_PHONE")
    private long phoneNumber;

    @ColumnInfo(name = "MENTOR_EMAIL")
    private String email;

    public Mentor(String name, long phoneNumber, String email)
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    //region getters and setters

    public int getMentorId()
    {
        return mentorId;
    }

    public void setMentorId(int mentorId)
    {
        this.mentorId = mentorId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

//endregion

}
