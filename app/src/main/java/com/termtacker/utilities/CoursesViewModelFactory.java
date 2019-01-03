package com.termtacker.utilities;

import android.app.Application;

import com.termtacker.activities.CoursesActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CoursesViewModelFactory implements ViewModelProvider.Factory
{
    private Application application;
    private int termId;
    private boolean showOnlyAvailableCourses;

    public CoursesViewModelFactory(Application application, int termId, boolean showOnlyAvailableCourses)
    {
        this.application = application;
        this.termId = termId;
        this.showOnlyAvailableCourses = showOnlyAvailableCourses;
    }

    @NonNull
    @Override
    public CourseViewModel create(@NonNull Class modelClass)
    {
        return new CourseViewModel(application, termId, showOnlyAvailableCourses);
    }
}
