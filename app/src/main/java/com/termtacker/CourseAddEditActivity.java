package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class CourseAddEditActivity extends AppCompatActivity
{
    private EditText editTextCourseTitle;
    private EditText editTextCourseCode;
    private EditText editTextCourseStart;
    private TextView textViewCourseEndLabel;
    private EditText editTextCourseEnd;

    private static final String TAG = TermAddEditActivity.class.getCanonicalName();
    public static final String EXTRA_ID = "com.termtracker.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.termtracker.EXTRA_TITLE";
    public static final String EXTRA_START = "com.termtracker.EXTRA_START";
    public static final String EXTRA_END = "com.termtracker.EXTRA_END";
    public static final String EXTRA_STATUS = "com.termtracker.EXTRA_STATUS";

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add_edit);
    }
}
