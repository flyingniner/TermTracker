package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AssessmentRepository
{
    private AssessmentDao assessmentDao;
    private LiveData<List<Assessment>> courseAssessments;

    public AssessmentRepository(Application application, int courseId)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.assessmentDao = database.assessmentDao();
        courseAssessments = assessmentDao.getAssessmentsForCourse(courseId);
    }

//region Getters
    public void insertAssessment(Assessment assessment)
    {
        new InsertAssessmentAsync(assessmentDao).execute();
    }

    public void updateAssessment(Assessment assessment)
    {
        new UpdateAssessmentAsync(assessmentDao).execute();
    }

    public void deleteAssessment(Assessment assessment)
    {
        new DeleteAssessmentAsync(assessmentDao).execute();
    }

    public LiveData<List<Assessment>> getCourseAssessments()
    {

        return courseAssessments;
    }
//endregion

//region AsyncCalls
    private static class InsertAssessmentAsync extends AsyncTask<Assessment,Void,Void>
    {
        private AssessmentDao assessmentDao;

        private InsertAssessmentAsync(AssessmentDao assessmentDao)
        {
            this.assessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments)
        {
            assessmentDao.insertAssessment(assessments[0]);
            return null;
        }
    }

    private static class UpdateAssessmentAsync extends AsyncTask<Assessment,Void,Void>
    {
        private AssessmentDao assessmentDao;

        private UpdateAssessmentAsync(AssessmentDao assessmentDao)
        {
            this.assessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments)
        {
            assessmentDao.updateAssessment(assessments[0]);
            return null;
        }
    }

    private static class DeleteAssessmentAsync extends AsyncTask<Assessment,Void,Void>
    {
        private AssessmentDao assessmentDao;

        private DeleteAssessmentAsync(AssessmentDao assessmentDao)
        {
            this.assessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments)
        {
            assessmentDao.deleteAssessment(assessments[0]);
            return null;
        }
    }
//endregion

}