package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CourseRepository
{
    private LiveData<List<Course>> termCourses;
    private CourseDao courseDao;

    public CourseRepository(Application application, int termId)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.courseDao = database.courseDao();
        termCourses = courseDao.getCoursesForTermByTermId(termId);
    }

//region Assessments
    public void insertAssessment(Course assessment)
    {
        new InsertCourseAsync(courseDao).execute();
    }

    public void updateAssessment(Course assessment)
    {
        new UpdateCourseAsync(courseDao).execute();
    }

    public void deleteAssessment(Course assessment)
    {
        new DeleteCourseAsync(courseDao).execute();
    }

    public LiveData<List<Course>> getCoursesForTerm()
    {
        return termCourses;
    }
//endregion

//region AsyncCalls
    private static class InsertCourseAsync extends AsyncTask<Course,Void,Void>
    {
        private CourseDao courseDao;

        private InsertCourseAsync(CourseDao assessmentDao)
        {
            this.courseDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Course... courses)
        {
            courseDao.insertCourse(courses[0]);
            return null;
        }
    }

    private static class UpdateCourseAsync extends AsyncTask<Course,Void,Void>
    {
        private CourseDao courseDao;

        private UpdateCourseAsync(CourseDao assessmentDao)
        {
            this.courseDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Course... courses)
        {
            courseDao.updateCourse(courses[0]);
            return null;
        }
    }

    private static class DeleteCourseAsync extends AsyncTask<Course,Void,Void>
    {
        private CourseDao courseDao;

        private DeleteCourseAsync(CourseDao assessmentDao)
        {
            this.courseDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Course... courses)
        {
            courseDao.deleteCourse(courses[0]);
            return null;
        }
    }
//endregion
}
