package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;

import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class CourseRepository
{
    private LiveData<List<Course>> courses;
    private CourseDao courseDao;
    private CardView cardView;

    public CourseRepository(Application application)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.courseDao = database.courseDao();
        courses = courseDao.getAllCourses();
    }


    public void insertCourse(Course course)
    {
        new InsertCourseAsync(courseDao).execute(course);
    }

    public void updateCourse(Course course)
    {
        new UpdateCourseAsync(courseDao).execute(course);
    }

    public void deleteCourse(Course course)
    {
        new DeleteCourseAsync(courseDao).execute(course);
    }

    public LiveData<List<Course>> getAllCourses()
    {
        return courses;
    }

    public LiveData<List<Course>> getCoursesForTerm(int termId)
    {
        //TODO: find a way to filter my list by term id. Can't do it on LiveData
//        LiveData<List<Course>> filteredData = Transformations.map(courses, newDataSet -> applyFilter(newDataSet, termId));
        LiveData<List<Course>> filteredData = Transformations.map(courses, newDataSet -> {
            List<Course> filteredCourses = new ArrayList<>();
            for (Course c : newDataSet)
            {
                if (c.getTermId() == termId)
                    filteredCourses.add(c);
            }
            return filteredCourses;
        });

        return filteredData;
    }

//    List<Course> applyFilter(List<Course> list, int termId)
//    {
//        List<Course> filteredCourses = new ArrayList<>();
//
//        for (Course item : list)
//        {
//            if (item.getTermId() == termId)
//                filteredCourses.add(item);
//        }
//
//        return filteredCourses;
//    }

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
