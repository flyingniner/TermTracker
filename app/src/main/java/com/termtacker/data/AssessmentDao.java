package com.termtacker.data;

import com.termtacker.models.Assessment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AssessmentDao
{
    @Insert()
    long insertAssessment(Assessment assessment);

    @Update()
    int updateAssessment(Assessment... assessments);

    @Query("SELECT * FROM ASSESSMENTS WHERE FK_COURSE_ID = :courseId")
    LiveData<List<Assessment>> getAssessmentsForCourse(int courseId);

    @Query("SELECT * FROM ASSESSMENTS")
    LiveData<List<Assessment>> getAssessments();

    @Delete()
    int deleteAssessment(Assessment assessment);
}
