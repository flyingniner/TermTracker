package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class AssessmentRepository
{
    private AssessmentDao assessmentDao;
    private LiveData<List<Assessment>> allAssessments;

    public AssessmentRepository(Application application)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.assessmentDao = database.assessmentDao();
        allAssessments = assessmentDao.getAssessments();
    }

    public AssessmentRepository(Application application, int courseId)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.assessmentDao = database.assessmentDao();
        allAssessments = assessmentDao.getAssessmentsForCourse(courseId);
    }

//region Getters
    public void insertAssessment(Assessment assessment)
    {
        new InsertAssessmentAsync(assessmentDao).execute(assessment);
    }

    public void updateAssessment(Assessment assessment)
    {
        new UpdateAssessmentAsync(assessmentDao).execute(assessment);
    }

    public void deleteAssessment(Assessment assessment)
    {
        new DeleteAssessmentAsync(assessmentDao).execute(assessment);
    }

    public LiveData<List<Assessment>> getCourseAssessments(int courseId)
    {
        LiveData<List<Assessment>> filteredData = Transformations.map(allAssessments, newDataSet -> {
            List<Assessment> filteredAssessments = new ArrayList<>();
            for (Assessment a : newDataSet)
            {
                if (a.getCourseId() == courseId)
                    filteredAssessments.add(a);
            }
            return filteredAssessments;
        });

        return filteredData;
    }

    public List<Assessment> getStaticCourseAssessments(int courseId)
    {
        List<Assessment> filteredAssessments = new ArrayList<>();
        allAssessments.getValue().forEach(x -> {
            if (x.getCourseId() == courseId)
                filteredAssessments.add(x);
        });
        return filteredAssessments;
    }

    public LiveData<List<Assessment>> getAllAssessments()
    {
        return allAssessments;
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
