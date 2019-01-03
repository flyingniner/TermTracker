package com.termtacker.data;

import com.termtacker.models.Course;

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

    @Query("SELECT COURSE_TITLE FROM COURSES " +
            "WHERE COURSE_STATUS <> 'COMPLETED' " +
            "ORDER BY COURSE_TITLE ASC")
    List<String> getTitlesForUnCompletedCourses();

    @Query("SELECT COURSE_TITLE FROM COURSES WHERE COURSE_ID = :courseId")
    String getCourseName(int courseId);

    @Query("SELECT COURSE_ID FROM COURSES WHERE COURSE_TITLE = :courseName")
    int getCourseId(String courseName);

    @Query("SELECT * FROM COURSES")
    List<Course> getStaticCourses();

    @Query("SELECT COURSE_NOTES FROM COURSES WHERE COURSE_ID = :courseId")
    String getCourseNotes(int courseId);

    @Query("UPDATE COURSES SET COURSE_NOTES = :note WHERE COURSE_ID = :courseId")
    void updateCourseNotes(int courseId, String note);

    @Query("SELECT COUNT(*) FROM COURSES WHERE FK_TERM_ID = :termId")
    int getCourseCountForTerm(int termId);

    @Query("SELECT * FROM COURSES WHERE COURSE_ID = :courseId")
    Course getCourseById(int courseId);

}
