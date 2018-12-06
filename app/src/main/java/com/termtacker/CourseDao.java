package com.termtacker;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CourseDao
{
    @Insert()
    void insertCourse(Course... course);

    @Update()
    int updateCourse(Course... course);

    @Query("SELECT * FROM COURSES")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM COURSES WHERE FK_TERM_ID = :termId")
    LiveData<List<Course>> getCoursesForTermByTermId(int termId);

    @Delete
    void deleteCourse(Course course);

}
