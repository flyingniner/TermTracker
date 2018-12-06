package com.termtacker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MentorDao
{
    @Insert
    void insertMentor(Mentor mentor);

    @Update
    void updateMentor(Mentor mentor);

    @Query("SELECT MENTOR_ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL FROM MENTORS, " +
            "COURSES WHERE MENTOR_ID = FK_MENTOR_ID " +
            "AND COURSE_ID = :courseId")
    LiveData<Mentor> getMentorByCourseId(int courseId);

    @Query("SELECT * FROM MENTORS WHERE MENTOR_NAME = :Name")
    Mentor getMentorByName(String Name);

    @Delete
    void deleteMentor(Mentor mentor);
}
