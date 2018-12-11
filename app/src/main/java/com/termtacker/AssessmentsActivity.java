package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AssessmentsActivity extends AppCompatActivity
{
    public static final String EXTRA_ID = "com.termtracker.EXTRA_ID";
    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.EXTRA_CODE";
//    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.EXTRA_ID";
//    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.EXTRA_ID";
//    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.EXTRA_ID";
//    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);
    }
}
