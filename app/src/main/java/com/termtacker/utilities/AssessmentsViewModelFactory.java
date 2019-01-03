package com.termtacker.utilities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AssessmentsViewModelFactory implements ViewModelProvider.Factory
{
    private Application application;
    private int courseId;


    public AssessmentsViewModelFactory(Application application, int courseId)
    {
        this.application = application;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public AssessmentViewModel create(@NonNull Class modelClass)
    {
        return new AssessmentViewModel(application, courseId);
    }
}
