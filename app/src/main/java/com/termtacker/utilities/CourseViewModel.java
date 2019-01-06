package com.termtacker.utilities;

import android.app.Application;

import com.termtacker.models.Course;
import com.termtacker.data.CourseRepository;
import com.termtacker.data.TermRepository;

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

    /**
     * This is the constructor for the Course View Model. It will provide 3 results:
     * 1) a list of all courses when termId == 0;
     * 2) a list of filtered courses assigned to a term when termId != 0 and showOnlyAvailableCourses == false
     * 3) a list of filtered courses that are not assigned to any term
     * @param application
     * @param termId
     * @param showOnlyAvailableCourses
     */
    public CourseViewModel(@NonNull Application application, int termId, boolean showOnlyAvailableCourses)
    {
        super(application);
        repository = new CourseRepository(application);
        termRepository = new TermRepository(application);

        if (termId == 0) //the calling activity wants a list of all courses
            allCourses = repository.getAllCourses();
        else if (termId != 0 && !showOnlyAvailableCourses) //this is for the view of assigned courses to a term
            allCourses = repository.getCoursesForTerm(termId);
        else //this is for a term seeking to assign a course to itself
            allCourses = repository.getCoursesForTerm(0);
    }

    public void insertCourse(Course course) { repository.insertCourse(course);}
    public void updateCourse(Course course) { repository.updateCourse(course);}
    public void deleteCourse(Course course) { repository.deleteCourse(course);}
    public Course getCourseById(int courseId) { return repository.getCourseById(courseId); }
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
    public LocalDate getTermStartDate(int termId) { return termRepository.getTermStartDate(termId); }

    public List<Course> getStaticFilteredCourses(int termId)
    {
        return repository.getStaticCoursesForTerm(termId);
    }

    public String getCourseNotes(int courseId)
    {
        String notes = repository.getCourseNotes(courseId);

        return notes;
    }

    public void updateCourseNotes(int courseId, String note)
    {
        repository.updateCourseNotes(courseId, note);
    }
}
