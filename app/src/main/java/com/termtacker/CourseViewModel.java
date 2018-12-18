package com.termtacker;

import android.app.Application;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CourseViewModel extends AndroidViewModel
{
    private CourseRepository repository;
    private TermRepository termRepository;
    private LiveData<List<Course>> allCourses;

    public CourseViewModel(@NonNull Application application)
    {
        super(application);
        repository = new CourseRepository(application);
        allCourses = repository.getAllCourses();
        termRepository = new TermRepository(application);
    }

    public void insertCourse(Course course) { repository.insertCourse(course);}
    public void updateCourse(Course course) { repository.updateCourse(course);}
    public void deleteCourse(Course course) { repository.deleteCourse(course);}

    public LiveData<List<Course>> getFilteredCourses(int termId)
    {
        return repository.getCoursesForTerm(termId);
    }

    public LiveData<List<Course>> getAllCourses()
    {
        return allCourses;
    }


    public String getCourseName(int courseId)
    {
        return repository.getCourseName(courseId);
    }

    public int getCourseId(String courseName)
    {
        return repository.getCourseId(courseName);
    }

    public LocalDate getTermEndDate(int termId)
    {
        return termRepository.getTermEndDate(termId);
    }

    public List<Course> getStaticFilteredCourses(int termId)
    {
        return repository.getStaticCoursesForTerm(termId);
    }
}
