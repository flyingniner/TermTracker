package com.termtacker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.termtacker.data.CourseRepository;
import com.termtacker.R;
import com.termtacker.models.Status;
import com.termtacker.utilities.Utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssessmentAddEditActivity extends AppCompatActivity
{
    //region EXTRAS
    public static final String EXTRA_ASSESSMENT_ID = "com.termtracker.AssessmentsAddEditActivity_EXTRA_ASSESSMENT_ID";
    public static final String EXTRA_ASSESSMENT_TITLE = "com.termtracker.AssessmentsAddEditActivity_EXTRA_ASSESSMENT_TITLE";
    public static final String EXTRA_ASSESSMENT_TYPE = "com.termtracker.AssessmentsAddEditActivity_EXTRA_ASSESSMENT_TYPE";
    public static final String EXTRA_ASSESSMENT_DATE = "com.termtracker.AssessmentsAddEditActivity_EXTRA_ASSESSMENT_DATE";
    //endregion
    public static final int ADD_ASSESSMENT_ALERT_REQUEST = 38;

    //region activity elements
    private EditText editTextAssessmentCode;
    private EditText editTextScheduled;
    private ImageButton datePicker;
    private DatePickerDialog datePickerDialog;
    private Spinner assessmentTypeSpinner;
    private Spinner courseNameSpinner;
    private CheckBox checkBoxResultPassed;
    private CheckBox checkBoxResultApproaching;
    private CheckBox checkBoxSetReminder;
    private Button buttonSave;
    //endregion
    //region fields
    private int assessmentId;
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

        if (intentFromCaller.hasExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_COURSE_ID)) {
            isCourseAddEditActivityCaller = true;
        }

        loadAssessmentTypeSpinner();
        loadCoursesSpinner();

        getUIReferences();

        setButtonListerners();

        loadFieldValues();
    }


    /**
     * Sets up button listeners
     */
    private void setButtonListerners()
    {
        checkBoxResultPassed.setOnClickListener(listener -> {
            if (checkBoxResultPassed.isChecked()) {
                checkBoxResultApproaching.setChecked(false);
            }
        });

        checkBoxResultApproaching.setOnClickListener(listener -> {
            if (checkBoxResultApproaching.isChecked()) {
                checkBoxResultPassed.setChecked(false);
            }
        });

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


        buttonSave.setOnClickListener(listener -> {
            saveAssessment(intentFromCaller);
        });
    }


    /**
     * Binds UI elements with their ids
     */
    private void getUIReferences()
    {
        editTextAssessmentCode = findViewById(
                R.id.assessment_add_edit_assessment_code);
        datePicker = findViewById(R.id.assessment_add_edit_date_picker);
        editTextScheduled = findViewById(R.id.assessment_add_edit_date);
        checkBoxResultPassed = findViewById(R.id.assessment_add_edit_passed);
        checkBoxResultApproaching = findViewById(R.id.assessment_add_edit_not_passed);
        buttonSave = findViewById(R.id.assessment_add_edit_save);
    }


    /**
     * Checks to see who the caller is, then loads the form's fields with
     * values from the caller's intentFromCaller.
     */
    private void loadFieldValues()
    {
        if (!isCourseAddEditActivityCaller) //if true, the caller was AssessmentsActivity
        {
            assessmentId = intentFromCaller.getIntExtra(AssessmentsActivity.EXTRA_ID, 0);
            String passedResult;
            if (assessmentId == 0) {
                passedResult = Status.PENDING;
            } else {
                passedResult = intentFromCaller.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT);

                editTextAssessmentCode.setText(intentFromCaller
                        .getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE));
                editTextScheduled.setText(intentFromCaller
                        .getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED));
            }

            if (passedResult.equals(Status.PASSED)) {
                checkBoxResultPassed.setChecked(true);
            } else if (passedResult.equals(Status.APPROACHING)) {
                checkBoxResultApproaching.setChecked(true);
            } else {
                checkBoxResultPassed.setChecked(false);
                checkBoxResultApproaching.setChecked(false);
            }
        }

        if (isCourseAddEditActivityCaller) //if true, the caller was CoursesAddEditActivity
        {
            assessmentId = intentFromCaller.getIntExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, 0);
            String passedResult;
            if (assessmentId == 0) {
                passedResult = Status.PENDING;
            } else {
                passedResult = intentFromCaller.getStringExtra(
                        CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT);

                editTextAssessmentCode.setText(
                        intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE));
                editTextScheduled.setText(
                        intentFromCaller.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED));
            }

            if (passedResult.equals(Status.PASSED)) {
                checkBoxResultPassed.setChecked(true);
            } else if (passedResult.equals(Status.APPROACHING)) {
                checkBoxResultApproaching.setChecked(true);
            } else {
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
        courseNameSpinner = findViewById(R.id.assessment_add_edit_parent_course_spinner);

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
            courseNameSpinner.setEnabled(false);
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
        String type = assessmentTypeSpinner.getSelectedItem().toString();

        String courseName = courseNameSpinner.getSelectedItem().toString();

        try {
            scheduled = Utils.convertStringDate(
                    editTextScheduled.getText().toString());
            if (scheduled == null)
                throw new NullPointerException();
        }
        catch (NullPointerException np) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.missing_date_value)
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }
        catch (DateTimeException dte) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.invalid_date_format)
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();

            return;
        }

        String result = determineResult(scheduled);

        Intent data = new Intent();

        if (!isCourseAddEditActivityCaller) //the caller was AssessmentsActivity
        {
            if (assessmentId > 0)
                data.putExtra(AssessmentsActivity.EXTRA_ID, assessmentId);

            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE, editTextAssessmentCode.getText().toString());
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE, type);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_COURSE_NAME, courseName);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT, result);
            data.putExtra(AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED, scheduled.toEpochDay());
        } else { //the caller was CoursesAddEditActivity

            if (assessmentId > 0) {
                data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, assessmentId);
            }

            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE, editTextAssessmentCode.getText().toString());
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_TYPE, type);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT, result);
            data.putExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED, scheduled.toEpochDay());


        }
        setResult(RESULT_OK, data);

        finish();
    }

    /**
     * Determines the status that should be marked for the current
     * assessment when being saved.
     *
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
        else if (scheduled.isBefore(LocalDate.now()))
            result = Status.INCOMPLETE;
        else
            result = Status.PENDING;

        return result;
    }


    /**
     * Creates the menu in the activity bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.assessment_add_edit_menu, menu);
        return true;
    }

    /**
     * Handler for when the menu item is selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_courses:
                intent = new Intent(this, CoursesActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_assessment_reminder:
                intent = new Intent(this, AssessmentAlertActivity.class);
                intent.putExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_ID, assessmentId);
                intent.putExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_TITLE, courseName);
                intent.putExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_TYPE, assessmentType);
                try {
                    scheduled = Utils.convertStringDate(editTextScheduled.getText().toString());
                } catch (NullPointerException np) {
                    Toast.makeText(this, "Date Format use use \"MM/DD/YYYY\"",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                intent.putExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_DATE,
                        scheduled.toEpochDay());

                startActivityForResult(intent, ADD_ASSESSMENT_ALERT_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
