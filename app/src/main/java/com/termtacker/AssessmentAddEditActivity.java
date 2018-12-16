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


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);


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


        Intent intent = getIntent();
        if (intent.hasExtra(AssessmentsActivity.EXTRA_ID)) {
            editTextAssessmentCode.setText(intent.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE));
            editTextScheduled.setText(intent.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED));

            String passedResult = intent.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT);

            if (passedResult.equals(Status.PASSED))
                checkBoxResultPassed.setChecked(true);
            else if (passedResult == Status.APPROACHING)
                checkBoxResultApproaching.setChecked(true);
            else
            {
                checkBoxResultPassed.setChecked(false);
                checkBoxResultApproaching.setChecked(false);
            }
        }

        buttonSave.setOnClickListener(listener -> {
            saveAssessment();
        });
    }

    private void loadAssessmentTypeSpinner()
    {
        assessmentTypeSpinner = findViewById(R.id.assessment_type_spinner);
//        assessmentTypeSpinner.setOnItemSelectedListener(this);

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
        Intent intent = getIntent();
        if (intent.hasExtra(AssessmentsActivity.EXTRA_ID))
        {
            int position = assessmentTypes.indexOf(intent.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE));
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

    private void loadCoursesSpinner()
    {
        courseNameSpinner= findViewById(R.id.assessment_add_edit_parent_course_spinner);
//        courseNameSpinner.setOnItemSelectedListener(this);

        CourseRepository repository = new CourseRepository(this.getApplication());
        List<String> courseNames = repository.getTitlesForNonCompletedCourses();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                courseNames
                );

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        courseNameSpinner.setAdapter(dataAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra(AssessmentsActivity.EXTRA_ID))
        {
            int position = courseNames.indexOf(intent.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_COURSE_NAME));
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
    private void saveAssessment()
    {
        int id = getIntent().getIntExtra(AssessmentsActivity.EXTRA_ID, 0);

            String code = editTextAssessmentCode.getText().toString();
            String type = assessmentTypeSpinner.getSelectedItem().toString();
            String courseName = courseNameSpinner.getSelectedItem().toString();
            String result;

        try
        {
            scheduled = Utils.convertStringDate(
                editTextScheduled.getText().toString()
            );
        }
        catch (NullPointerException np)
        {

            Toast.makeText(this,
                    "Date Format use use \"MM/DD/YYYY\"",
                    Toast.LENGTH_LONG).show();
            return;
        }

        result = determineResult(scheduled);


        Intent data = new Intent();

        //TODO: determine where the call is from (ie. from AssessmentsActivity or CourseAddEditActivity
        if (id > 0)
            data.putExtra(AssessmentsActivity.EXTRA_ID,id);
        data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE, code);

        if (type == "")
        {
            Toast.makeText(this,
                    "Please select an assessment type!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE, type);

        if(courseName == "")
        {
            Toast.makeText(this,
                    "Please select a course name!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_COURSE_NAME, courseName);
        data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT, result);
        data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED, scheduled.toEpochDay());

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
