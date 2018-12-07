package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static com.termtacker.TermAddEditActivity.EXTRA_CURRENT_TERM_ID;

public class CourseAddEditActivity extends AppCompatActivity
{
    //region Layout items
    private EditText editTextCourseTitle;
//    private EditText editTextCourseCode;
    private EditText editTextCourseStart;
    private TextView textViewCourseEndLabel;
    private EditText editTextCourseEnd;
    private ImageView startDatePicker;
    private ImageView endDatePicker;
    private Button saveButton;
    private Button addAssessmentsButton;
    private Button addNoteButton;
    //endregion

    private static final String TAG = TermAddEditActivity.class.getCanonicalName();

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add_edit);

        Intent intent = getIntent();
        int termId = intent.getIntExtra(EXTRA_CURRENT_TERM_ID, -1);
        editTextCourseTitle = findViewById(R.id.course_add_edit_title);
//        editTextCourseCode = findViewById(R.id.course_add_edit_code);
        editTextCourseStart = findViewById(R.id.course_add_edit_start);
        textViewCourseEndLabel = findViewById(R.id.course_add_edit_end_label);
        editTextCourseEnd = findViewById(R.id.course_add_edit_end);
        startDatePicker = findViewById(R.id.course_add_edit_start_date_picker);
        endDatePicker = findViewById(R.id.course_add_edit_end_date_picker);
        saveButton = findViewById(R.id.course_add_edit_save);
        addAssessmentsButton = findViewById(R.id.course_add_edit_add_assessement);
        addNoteButton = findViewById(R.id.course_add_edit_notes);

        RecyclerView recyclerView = findViewById(R.id.course_add_edit_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CourseAdapter courseAdapter = new CourseAdapter();
        recyclerView.setAdapter(courseAdapter);

        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        courseViewModel.getFilteredCourses(termId).observe(this, courses -> courseAdapter.submitList(courses));

        courseAdapter.setOnItemClickListener(new CourseAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(Course course)
            {

            }
        });
    }
}
