package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

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

    /**
     * Gets the list of course titles where the course has not been
     * marked as "complete".
     *
     * Note: courses can only be marked complete if all assessessments
     * that were added to the course have been marked as passed during
     * the term.
     * @return
     */
    public List<String> getTitlesForNonCompletedCourses()
    {
        return courseDao.getTitlesForUnCompletedCourses();
    }

    public List<Course> getStaticCoursesForTerm(int termId)
    {

        List<Course> courses = courseDao.getStaticCourses();
        List<Course> filtered = new ArrayList<>();
        courses.forEach(x -> {
            if (x.getTermId() == termId)
                filtered.add(x);
                }
        );

        return filtered;



    }

    public LiveData<List<Course>> getCoursesForTerm(int termId)
    {
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

    public String getCourseName(int courseId)
    {
        //TODO: this may need to be done async...
        return courseDao.getCourseName(courseId);
    }

    public int getCourseId(String courseName)
    {
        return courseDao.getCourseId(courseName);
    }


//region AsyncCalls
    private static class InsertCourseAsync extends AsyncTask<Course,Void,Void>
    {
        private CourseDao courseDao;

        private InsertCourseAsync(CourseDao courseDao)
        {
            this.courseDao = courseDao;
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

        private UpdateCourseAsync(CourseDao courseDao)
        {
            this.courseDao = courseDao;
        }

        @Override
        protected Void doInBackground(Course... courses)
        {
            Log.d("CourseRepo", "Id: " +
                  courses[0].getCourseId() + "\r\nTitle: " +
                    courses[0].getTitle());
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
