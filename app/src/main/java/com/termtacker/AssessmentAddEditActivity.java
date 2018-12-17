package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssessmentAddEditActivity extends AppCompatActivity
{

    //region activity elements
    private EditText editTextAssessmentCode;
    private EditText editTextScheduled;
    private ImageButton datePicker;
    private DatePickerDialog datePickerDialog;
    private Spinner assessmentTypeSpinner;
    private Spinner courseNameSpinner;
    private CheckBox checkBoxResultPassed;
    private CheckBox checkBoxResultApproaching;
    private Button buttonSave;
    //endregion

    //region fields
    private String assessmentType;
    private String courseName;
    private Calendar calendar;
    private LocalDate scheduled;
    private boolean isCourseAddEditActivityCaller = false;
    Intent intentFromCaller;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        intentFromCaller = getIntent();

        if (intentFromCaller.hasExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID))
            isCourseAddEditActivityCaller = true;

        loadAssessmentTypeSpinner();
        loadCoursesSpinner();

        editTextAssessmentCode = findViewById(
                R.id.assessment_add_edit_assessment_code);
        datePicker = findViewById(R.id.assessment_add_edit_date_picker);
        editTextScheduled = findViewById(R.id.assessment_add_edit_date);
        checkBoxResultPassed = findViewById(R.id.assessment_add_edit_passed);
        checkBoxResultApproaching = findViewById(R.id.assessment_add_edit_not_passed);
        buttonSave = findViewById(R.id.assessment_add_edit_save);

        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(
                        AssessmentAddEditActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)

                    {
                        editTextScheduled.setText(
                                (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });


        loadFieldValues();

        buttonSave.setOnClickListener(listener -> {
            saveAssessment(intentFromCaller);
        });
    }



    /**
     * Checks to see who the caller is, then loads the form's fields with
     * values from the caller's intent.
     */
    private void loadFieldValues()
    {
        if (!isCourseAddEditActivityCaller) //if true, the caller was AssessmentsActivity
        {
            editTextAssessmentCode.setText(intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE));
            editTextScheduled.setText(intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED));

            String passedResult = intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT);

            if (passedResult.equals(Status.PASSED))
                checkBoxResultPassed.setChecked(true);
            else if (passedResult.equals(Status.APPROACHING))
                checkBoxResultApproaching.setChecked(true);
            else
            {
                checkBoxResultPassed.setChecked(false);
                checkBoxResultApproaching.setChecked(false);
            }
        }

        if (isCourseAddEditActivityCaller) //if true, the caller was CoursesAddEditActivity
        {
            editTextAssessmentCode.setText(
                    intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE));
            editTextScheduled.setText(
                    intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED));

            String passedResult = intentFromCaller.getStringExtra(
                    CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT);

            if (passedResult.equals(Status.PASSED))
                checkBoxResultPassed.setChecked(true);
            else if (passedResult.equals(Status.APPROACHING))
                checkBoxResultApproaching.setChecked(true);
            else
            {
                checkBoxResultPassed.setChecked(false);
                checkBoxResultApproaching.setChecked(false);
            }
        }
    }



    /**
     * Loads the assessment type spinner. If this is an existing assessment,
     * the AssessmentType is displayed.
     */
    private void loadAssessmentTypeSpinner()
    {
        assessmentTypeSpinner = findViewById(R.id.assessment_type_spinner);

        List<String> assessmentTypes = new ArrayList<>();
        assessmentTypes.add(getResources().getString(R.string.assessmentTypePre));
        assessmentTypes.add(getResources().getString(R.string.assessmentTypeObj));
        assessmentTypes.add(getResources().getString(R.string.assessmentTypePrj));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                assessmentTypes);

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        assessmentTypeSpinner.setAdapter(dataAdapter);

        if (!isCourseAddEditActivityCaller) //the caller was AssessmentsActivity
        {
            int position = assessmentTypes.indexOf(
                    intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE));
            assessmentTypeSpinner.setSelection(position);
        }

        if (isCourseAddEditActivityCaller) //the caller was CoursesAddEditActivity
        {
            int position = assessmentTypes.indexOf(
                    intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_TYPE));
            assessmentTypeSpinner.setSelection(position);
        }

        assessmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                assessmentType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                assessmentType = "";
            }
        });
    }



    /**
     * Loads the course name spinner. If this is an existing assessment,
     * the Course Name is displayed.
     */
    private void loadCoursesSpinner()
    {
        courseNameSpinner= findViewById(R.id.assessment_add_edit_parent_course_spinner);

        CourseRepository repository = new CourseRepository(this.getApplication());
        List<String> courseNames = repository.getTitlesForNonCompletedCourses();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                courseNames
                );

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        courseNameSpinner.setAdapter(dataAdapter);

        if (!isCourseAddEditActivityCaller) //the caller was AssessmentsActivity
        {
            int position = courseNames.indexOf(
                    intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_COURSE_NAME));
            courseNameSpinner.setSelection(position);
        }

        if (isCourseAddEditActivityCaller) //the caller was CourseAddEditActivity
        {
            int position = courseNames.indexOf(
                    intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_COURSE_NAME));
            courseNameSpinner.setSelection(position);
        }

        courseNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                courseName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                courseName = "";
            }
        });
    }



    /**
     * Validates the user's input and submits the result to the
     * calling activity
     */
    private void saveAssessment(Intent intentFromCaller)
    {


        String code = editTextAssessmentCode.getText().toString();

        String type = assessmentTypeSpinner.getSelectedItem().toString();
        if (type.equals(""))
        {
            Toast.makeText(this,
                    "Please select an assessment type!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String courseName = courseNameSpinner.getSelectedItem().toString();
        if(courseName.equals(""))
        {
            Toast.makeText(this,
                    "Please select a course name!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try
        {
            scheduled = Utils.convertStringDate(
                editTextScheduled.getText().toString());
        }
        catch (NullPointerException np)
        {
            Toast.makeText(this,
                    "Date Format use use \"MM/DD/YYYY\"",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String result = determineResult(scheduled);

        Intent data = new Intent();

        if (!isCourseAddEditActivityCaller) //the caller was AssessmentsActivity
        {
            int id = getIntent().getIntExtra(AssessmentsActivity.EXTRA_ID, 0);
            if (id > 0)
                data.putExtra(AssessmentsActivity.EXTRA_ID,id);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE, code);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE, type);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_COURSE_NAME, courseName);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT, result);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED, scheduled.toEpochDay());

        }

        else //the caller was CoursesAddEditActivity
        {
            int id = getIntent().getIntExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, 0);
            if (id > 0)
                data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID,id);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE, code);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_TYPE, type);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_COURSE_NAME, courseName);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT, result);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED, scheduled.toEpochDay());
        }

        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Determines the status that should be marked for the current
     * assessment when being saved.
     * @param scheduled
     * @return
     */
    private String determineResult(LocalDate scheduled)
    {
        String result;
        if (checkBoxResultApproaching.isChecked())
            result = Status.APPROACHING;
        else if (checkBoxResultPassed.isChecked())
            result = Status.PASSED;
        else if (scheduled.isAfter(LocalDate.now()))
            result = Status.INCOMPLETE;
        else
            result = Status.PENDING;

        return result;
    }

}
