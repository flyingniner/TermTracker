package com.termtacker.utilities;

import android.app.Application;

import com.termtacker.models.Assessment;
import com.termtacker.data.AssessmentRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AssessmentViewModel extends AndroidViewModel
{
    private AssessmentRepository assessmentRepo;
//    private LiveData<List<Assessment>> courseAssessments;
    private LiveData<List<Assessment>> allAssessments;

    public AssessmentViewModel(@NonNull Application application, int courseId)
    {
        super(application);
        assessmentRepo = new AssessmentRepository(application);

        if (courseId == 0)
            allAssessments = assessmentRepo.getAllAssessments();
        else
            allAssessments = assessmentRepo.getCourseAssessments(courseId);
    }

//    public AssessmentViewModel(@NonNull Application application)
//    {
//        super(application);
//        assessmentRepo = new AssessmentRepository(application);
//        allAssessments = assessmentRepo.getAllAssessments();
//    }

    public void insertAssessment(Assessment assessment)
    {
        assessmentRepo.insertAssessment(assessment);
    }

    public void updateAssessment(Assessment assessment)
    {
        assessmentRepo.updateAssessment(assessment);
    }

    public void deleteAssessment(Assessment assessment)
    {
        assessmentRepo.deleteAssessment(assessment);
    }

    public LiveData<List<Assessment>> getAllAssessments()
    {
        return allAssessments;
    }

    public LiveData<List<Assessment>> getAssessmentsForCourse(int courseId)
    {
        return assessmentRepo.getCourseAssessments(courseId);
    }

    public List<Assessment> getNonObservableAssessmentsForCourse(int courseId)
    {
        return assessmentRepo.getStaticCourseAssessments(courseId);
    }

    public int getAssessmentCountForCourse(int courseId)
    {
        int count;

        try {
            count = assessmentRepo.getAssessmentCountForCourse(courseId);
        }
        catch (NullPointerException npe)
        {
            return 0;
        }
        return count;
    }

}
