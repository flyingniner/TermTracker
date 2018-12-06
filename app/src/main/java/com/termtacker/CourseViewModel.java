package com.termtacker;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CourseViewModel extends AndroidViewModel
{
    private CourseRepository repository;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Course>> filteredCourses;

    public CourseViewModel(@NonNull Application application)
    {
        super(application);
        repository = new CourseRepository(application);
        allCourses = repository.getAllCourses();
    }

    public void insertCourse(Course course) { repository.insertCourse(course);}
    public void updateCourse(Course course) { repository.insertCourse(course);}
    public void deleteCourse(Course course) { repository.insertCourse(course);}

    public LiveData<List<Course>> getFilteredCourses(int termId)
    {
        return repository.getCoursesForTerm(termId);
    }
}
