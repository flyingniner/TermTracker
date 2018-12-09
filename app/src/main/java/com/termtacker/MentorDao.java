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

    @Query("SELECT MENTOR_ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL FROM MENTORS " +
            "JOIN COURSES ON MENTOR_ID = FK_MENTOR_ID " +
            "WHERE COURSE_ID = :courseId") //TODO: query so that only one result can be returned, not that there should be more than one.
    Mentor getMentorByCourseId(int courseId);
//    LiveData<Mentor> getMentorByCourseId(int courseId);
//            "COURSES WHERE MENTOR_ID = FK_MENTOR_ID " +

    @Query("SELECT * FROM MENTORS WHERE MENTOR_NAME = :Name")
    Mentor getMentorByName(String Name);

    @Delete
    void deleteMentor(Mentor mentor);
}
