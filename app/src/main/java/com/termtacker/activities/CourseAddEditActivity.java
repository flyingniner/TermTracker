package com.termtacker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.termtacker.data.CourseRepository;
import com.termtacker.models.AssessmentAdapter;
import com.termtacker.data.MentorRepository;
import com.termtacker.R;
import com.termtacker.models.Status;
import com.termtacker.models.Assessment;
import com.termtacker.models.Course;
import com.termtacker.models.Mentor;
import com.termtacker.utilities.AssessmentViewModel;
import com.termtacker.utilities.AssessmentsViewModelFactory;
import com.termtacker.utilities.CourseViewModel;
import com.termtacker.utilities.CoursesViewModelFactory;
import com.termtacker.utilities.Utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseAddEditActivity extends AppCompatActivity
{
    //region requests
    public static final int SELECT_MENTOR_REQUEST = 7;
    public static final int VIEW_NOTES_REQUEST = 23;
    private static final int ADD_ASSESSMENT_REQUEST = 31;
    public static final int EDIT_ASSESSMENT_REQUEST = 32;
    public static final int ADD_COURSE_ALERT_REQUEST = 34;
    private static final int DELETE_ASSESSMENT_REQUEST = 61;
    //endregion
    //region assessment extras
    public static final String EXTRA_ASSESSMENT_ID =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_ID";
    public static final String EXTRA_ASSESSMENT_CODE =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_CODE";
    public static final String EXTRA_ASSESSMENT_TYPE =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_TYPE";
    public static final String EXTRA_ASSESSMENT_SCHEDULED =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_SCHEDULED";
    public static final String EXTRA_ASSESSMENT_COURSE_ID =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESMENT_COURSE_ID";
    public static final String EXTRA_ASSESSMENT_RESULT =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_RESULT";
    public static final String EXTRA_ASSESSMENT_COURSE_NAME =
            "com.termtracker.CourseAddEditActivity_EXTRA_ASSESSMENT_COURSE_NAME";

    //endregion
    //region alert extras
    public static final String EXTRA_ALERT_COURSE_ID =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_ID";
    public static final String EXTRA_ALERT_COURSE_NAME =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_NAME";
    public static final String EXTRA_ALERT_COURSE_START =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_START";
    public static final String EXTRA_ALERT_COURSE_END =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_END";
    //endregion
    //region notes extras
    public static final String EXTRA_NOTES_COURSE_ID =
            "com.termtracker.CourseAddEditActivity_EXTRA_NOTES_COURSE_ID";
    public static final String EXTRA_NOTES_COURSE_NAME =
            "com.termtracker.CourseAddEditActivity_EXTRA_NOTES_COURSE_NAME";
    public static final String EXTRA_NOTES_COURSE_NOTETEXT =
            "com.termtracker.CourseAddEditActivity_EXTRA_NOTES_COURSE_NOTETEXT";
    //endregion

    //region Layout items
    private EditText editTextCourseTitle;
    private EditText editTextCourseStart;
    private TextView textViewCourseEndLabel;
    private EditText editTextCourseEnd;
    private ImageView startDatePicker;
    private ImageView endDatePicker;
    private EditText editTextCourseStatus;
    private Button contactCourseMentor;
    private Button saveButton;
    private Button assessmentsButton;
    private Button addNoteButton;
    //endregion
    //region fields
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private CourseViewModel courseViewModel;
    private AssessmentViewModel assessmentViewModel;
    private boolean isExistingCourse = false;
    private int courseId;
    private int mentorId;
    private int termId;
    private LocalDate termStartDate;
    private LocalDate termEndDate;
    private Intent intentFromCaller;
    private boolean isTermAddEditActivityCaller = false;
    private AssessmentAdapter assessmentAdapter;
    RecyclerView assessmentsRecyclerView;
    private boolean isNewCourseRequest;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);


        getUIFieldReferences();

        intentFromCaller = getIntent();

        isNewCourseRequest = intentFromCaller.hasExtra(CoursesActivity.EXTRA_IS_NEW_COURSE) ? true : false;
        isTermAddEditActivityCaller = intentFromCaller.hasExtra(TermAddEditActivity.EXTRA_TERM_ID) ? true : false;

        loadExistingCourseDetails(intentFromCaller);

        setUpAssessmentRecyclerView();

        AssessmentsViewModelFactory assessmentsFactory =
                new AssessmentsViewModelFactory(getApplication(), courseId);
        assessmentViewModel = ViewModelProviders.of(this, assessmentsFactory)
                .get(AssessmentViewModel.class);

        CoursesViewModelFactory coursesFactory =
                new CoursesViewModelFactory(getApplication(), termId, false);
        courseViewModel = ViewModelProviders.of(this, coursesFactory)
                .get(CourseViewModel.class);

        if (courseId > 0) { //only calls for data if it's an existing course
            assessmentViewModel.getAllAssessments().observe(this, list -> assessmentAdapter.submitList(list));
        }

        setupAdapterListeners();
        setButtonListeners();
    }


    /**
     * Sets up the listeners for the AssessmentAadapter to respond to either a "tap" or a
     * "long press"
     */
    private void setupAdapterListeners()
    {
        //long press or click for deleteTerm
        assessmentAdapter.setOnLongItemClickListener(assessment -> {
            requestDeleteConfirmation(assessment);
        });

        //tap for assessment details
        assessmentAdapter.setOnItemClickListener(assessment -> {
            Intent assessmentDataIntent = new Intent(CourseAddEditActivity.this,
                    AssessmentAddEditActivity.class);
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_ID, assessment.getAssessmentId());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_CODE, assessment.getAssessmentCode());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_TYPE, assessment.getAssessmentType());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_SCHEDULED, assessment.getAssessmentDate()
                    .format(Utils.dateFormatter_MMddyyyy));
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_COURSE_ID, assessment.getCourseId());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_RESULT, assessment.getResult());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_COURSE_NAME, courseViewModel.getCourseName(courseId));

            startActivityForResult(assessmentDataIntent, EDIT_ASSESSMENT_REQUEST);
        });
    }


    /**
     * Initializes the Assessment Recycler
     */
    private void setUpAssessmentRecyclerView()
    {
        assessmentsRecyclerView = findViewById(R.id.course_add_edit_recycler_view);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRecyclerView.setHasFixedSize(true);

        assessmentAdapter = new AssessmentAdapter();
        assessmentsRecyclerView.setAdapter(assessmentAdapter);
    }


    /**
     * Presents the user with a confirmation dialog when a delete assessment request is initiated.
     *
     * @param assessment
     */
    private void requestDeleteConfirmation(Assessment assessment)
    {
        boolean response = false;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_assessment_title))
                .setMessage(getString(R.string.assessment_confirm_delete_message))
                .setIcon(R.drawable.ic_incomplete_72dp)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        assessmentViewModel.deleteAssessment(assessment);
                        Toast.makeText(CourseAddEditActivity.this,
                                getString(R.string.delete_successful), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }


    /**
     * Initializes the UI elements
     */
    private void getUIFieldReferences()
    {
        editTextCourseTitle = findViewById(R.id.course_add_edit_title);
        editTextCourseStart = findViewById(R.id.course_add_edit_start);
        textViewCourseEndLabel = findViewById(R.id.course_add_edit_end_label);
        editTextCourseEnd = findViewById(R.id.course_add_edit_end);
        startDatePicker = findViewById(R.id.course_add_edit_start_date_picker);
        endDatePicker = findViewById(R.id.course_add_edit_end_date_picker);
        contactCourseMentor = findViewById(R.id.course_add_edit_mentor);
        saveButton = findViewById(R.id.course_add_edit_save);
        assessmentsButton = findViewById(R.id.course_add_edit_add_assessement);
        addNoteButton = findViewById(R.id.course_add_edit_notes);
    }


    /**
     * Prefills the form elements with the existing course details and
     * sets some global fields used in other methods
     *
     * @param intent
     */
    private void loadExistingCourseDetails(Intent intent)
    {
        if (!isNewCourseRequest) {
            if (!isTermAddEditActivityCaller) { //Caller is CoursesActivity

                loadDetailsForCoursesActivityCaller(intent);

            } else if (isTermAddEditActivityCaller) { //Caller is TermAddEditActivity

                loadDetailsForTermAddEditActivityCaller(intent);

            }
        } else { //This is new course so nothing to load

            setTitle("Add Course");
            contactCourseMentor.setText("Select a Mentor");
            courseId = 0;
        }

    }


    /**
     * Initializes UI field elements with values
     *
     * @param intent
     */
    private void loadDetailsForTermAddEditActivityCaller(Intent intent)
    {
        if (intent.getIntExtra(TermAddEditActivity.EXTRA_COURSE_ID, 0) > 0) {
            setTitle("Edit Course");
            isExistingCourse = true;

            courseId = intent.getIntExtra(TermAddEditActivity.EXTRA_COURSE_ID, 0);
            mentorId = intent.getIntExtra(TermAddEditActivity.EXTRA_MENTOR_ID, 0);
            termId = intent.getIntExtra(TermAddEditActivity.EXTRA_TERM_ID, 0);

            editTextCourseTitle.setText(intent.getStringExtra(TermAddEditActivity.EXTRA_TITLE));
            editTextCourseStart.setText(
                    LocalDate.ofEpochDay(
                            intent.getLongExtra(TermAddEditActivity.EXTRA_START, 0))
                            .format(Utils.dateFormatter_MMddyyyy));

            editTextCourseEnd.setText(
                    LocalDate.ofEpochDay(
                            intent.getLongExtra(TermAddEditActivity.EXTRA_COURSE_END, 0))
                            .format(Utils.dateFormatter_MMddyyyy));

            contactCourseMentor.setText("Contact Course Mentor");

            String status = intent.getStringExtra(TermAddEditActivity.EXTRA_STATUS);
            if (status == null)
                textViewCourseEndLabel.setText("Goal");
            else
                textViewCourseEndLabel.setText("Completed");

            termStartDate = LocalDate.ofEpochDay(
                    intent.getLongExtra(CoursesActivity.EXTRA_TERM_START, 0));

            termEndDate = LocalDate.ofEpochDay(
                    intent.getLongExtra(TermAddEditActivity.EXTRA_TERM_END, 0));
        }
    }


    /**
     * Initializes UI field elements with values
     *
     * @param intent
     */
    private void loadDetailsForCoursesActivityCaller(Intent intent)
    {
        setTitle("Edit Course");
        isExistingCourse = true;
        courseId = intent.getIntExtra(CoursesActivity.EXTRA_ID, 0);
        mentorId = intent.getIntExtra(CoursesActivity.EXTRA_MENTORID, 0);
        termId = intent.getIntExtra(CoursesActivity.EXTRA_TERMID, 0);

        if (termId > 0) {
            termStartDate = LocalDate.ofEpochDay(
                    intent.getLongExtra(CoursesActivity.EXTRA_TERM_START, 0));

            termEndDate = LocalDate.ofEpochDay(
                    intent.getLongExtra(CoursesActivity.EXTRA_TERM_END, 0));
        }


        editTextCourseTitle.setText(intent.getStringExtra(CoursesActivity.EXTRA_TITLE));
        editTextCourseStart.setText(
                LocalDate.ofEpochDay(
                        intent.getLongExtra(CoursesActivity.EXTRA_START, 0))
                        .format(Utils.dateFormatter_MMddyyyy));

        editTextCourseEnd.setText(
                LocalDate.ofEpochDay(
                        intent.getLongExtra(CoursesActivity.EXTRA_END, 0))
                        .format(Utils.dateFormatter_MMddyyyy));

        contactCourseMentor.setText("Contact Course Mentor");

        String status = intent.getStringExtra(CoursesActivity.EXTRA_STATUS);
        if (status == null) {

            textViewCourseEndLabel.setText("Goal");
        } else {

            textViewCourseEndLabel.setText("Completed");
        }
    }


    /**
     * Creates the listeners the the date pickers, save button,
     * assessments, and notes buttons
     * and
     */
    private void setButtonListeners()
    {
        setupStartDatePicker();

        setupEndDatePicker();

        setupSaveButtonListener();

        setupAssessmentListener();

        setupCourseMentorListener();

        setupNotesListener();

    }


    /**
     * Initializes the Notes button listener
     */
    private void setupNotesListener()
    {
        addNoteButton.setOnClickListener(listener -> {

            if (courseId < 1) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.course_add_note_error)
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
            } else {

                Intent data = new Intent(CourseAddEditActivity.this, NoteAddEditActivity.class);
                data.putExtra(EXTRA_NOTES_COURSE_ID, courseId);
                data.putExtra(EXTRA_NOTES_COURSE_NAME, editTextCourseTitle.getText().toString());

                CourseRepository courseRepository = new CourseRepository(getApplication());
                String notes = courseRepository.getCourseNotes(courseId);

                if (notes != null && !notes.isEmpty()) {

                    data.putExtra(EXTRA_NOTES_COURSE_NOTETEXT, notes);
                }

                startActivityForResult(data, VIEW_NOTES_REQUEST);
            }
        });
    }


    /**
     * Initializes the Course Mentor button listener
     */
    private void setupCourseMentorListener()
    {
        contactCourseMentor.setOnClickListener(listener -> {

            if (contactCourseMentor.getText().toString().equals("Select a Mentor"))
                onButtonSelectAMentorClick();
            else
                onButtonViewMentorContactClick(new View(this));
        });
    }


    /**
     * Initializes the Save button listener
     */
    private void setupSaveButtonListener()
    {
        saveButton.setOnClickListener(listener ->
        {
            saveCourse();
        });
    }


    /**
     * Initializes the Assessments button listener
     */
    private void setupAssessmentListener()
    {
        assessmentsButton.setOnClickListener(listener -> {

            if (isExistingCourse) {

                Intent data = new Intent(CourseAddEditActivity.this,
                        AssessmentAddEditActivity.class);
                data.putExtra(EXTRA_ASSESSMENT_COURSE_ID, courseId);
                data.putExtra(EXTRA_ASSESSMENT_COURSE_NAME, editTextCourseTitle.getText().toString());

                startActivityForResult(data, ADD_ASSESSMENT_REQUEST);
            } else {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.course_add_assessment_error)
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return;
            }
        });
    }


    /**
     * Initializes the End Date datepicker
     */
    private void setupEndDatePicker()
    {
        endDatePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(
                        CourseAddEditActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        editTextCourseEnd.setText(
                                (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }


    /**
     * Initializes the Start date datepicker
     */
    private void setupStartDatePicker()
    {
        startDatePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(
                        CourseAddEditActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)

                    {
                        editTextCourseStart.setText(
                                (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });
    }

    /**
     * Creates a Course object and validates user input and provides
     * the user with feedback if a field does not meet input requirements.
     *
     * @return
     */
    private Course createCourseObj()
    {
        Course c = new Course();
        String title = null;
        LocalDate start = null, end = null;


        try {

            title = editTextCourseTitle.getText().toString().trim();
            if (title.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(getString(R.string.missing_course_title))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return null;
            }

            String startStr = editTextCourseStart.getText().toString();
            if (startStr.isEmpty()) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(getString(R.string.missing_date_value))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return null;
            }
            String[] s = startStr.split("/");
            start = LocalDate.of(Integer.parseInt(s[2]), Integer.parseInt(s[0]), Integer
                    .parseInt(s[1]));


            String endStr = editTextCourseEnd.getText().toString();
            if (endStr.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(getString(R.string.missing_date_value))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return null;
            }


            String[] e = endStr.split("/");
            end = LocalDate.of(Integer.parseInt(e[2]), Integer.parseInt(e[0]), Integer
                    .parseInt(e[1]));
        } catch (NullPointerException npe) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(getString(R.string.null_field_value))
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return null;
        } catch (DateTimeException dte) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(getString(R.string.invalid_date_format))
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return null;
        }


        if (!checkDatesAreAcceptable(start, end, termStartDate, termEndDate))
            return null;

        String status = determineCourseStatus(termId, courseId, assessmentViewModel, termEndDate);

        c.setTitle(title);
        c.setTermId(termId);
        c.setStartDate(start);
        c.setEndDate(end);
        c.setStatus(status);
        c.setCourseMentorId(mentorId);

        if (isExistingCourse) {
            c.setCourseId(courseId);
        }

        return c;
    }

    /**
     * verifies the course start and end dates are valid for the term and
     * that the end date does not come before the start date.
     *
     * @param start
     * @param end
     * @param termEndDate
     * @return
     */
    private boolean checkDatesAreAcceptable(LocalDate start, LocalDate end, LocalDate termStartDate, LocalDate termEndDate)
    {
        //check the start date is not after end date,
        if (end.isBefore(start)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage("The ending date must come on or before the course end date")
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return false;
        }

        //if applicable, check the course dates fall with the assigned term dates
        if (termEndDate != null) {
            if (end.isAfter(termEndDate)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(getString(R.string.course_end_date_error) + termEndDate.format(Utils.dateFormatter_MMddyyyy))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
                return false;
            }

            if (start.isBefore(termStartDate)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_title)
                        .setMessage(getString(R.string.course_start_date_error) + termStartDate.format(Utils.dateFormatter_MMddyyyy));
            }
        }

        //if landing here, the dates are acceptable
        return true;
    }

    /**
     * Determines the status of the course based on the result of the
     * associated assessments. If no assessments present, the status will
     * show pending. To show as passed, both Objective and Performance
     * assessments must have at least one assessment marked as passed.
     *
     * @return
     */
    public static String determineCourseStatus(int termId, int courseId, AssessmentViewModel assessmentViewModel, LocalDate termEndDate)
    {
        LocalDate now = LocalDate.now();

        String objStatus = "";
        String perStatus = "";
        boolean hasObjectiveAssessment = false;
        boolean hasPerformanceAssessment = false;

        if (termId == 0) //first check if the course is assigned to a term, if not, mark as pending
            return Status.PENDING;

        if (courseId > 0) { //if this is an existing course, determine course status by looking at it's assessments
            List<Assessment> assessments;
            try {
                assessments = assessmentViewModel.getNonObservableAssessmentsForCourse(courseId);
            } catch (NullPointerException npe) {
                assessments = new ArrayList<>();
            }
            if (assessments.size() > 0) {
                for (Assessment a : assessmentViewModel.getNonObservableAssessmentsForCourse(courseId)) {
                    if (a.getAssessmentType().equals("Objective")) {
                        hasObjectiveAssessment = true;
                        if (a.getResult().equals(Status.PASSED)) {
                            objStatus = Status.PASSED;
                        }
                    }
                    if (a.getAssessmentType().equals("Performance")) {
                        hasPerformanceAssessment = true;
                        if (a.getResult().equals(Status.PASSED)) {
                            perStatus = Status.PASSED;
                        }
                    }
                }
            }

            if (hasObjectiveAssessment && hasPerformanceAssessment) {
                if (objStatus.equals(Status.PASSED) && perStatus.equals(Status.PASSED)) {
                    return Status.COMPLETED;
                }
            } else if (hasObjectiveAssessment && !hasPerformanceAssessment) {
                if (objStatus.equals(Status.PASSED)) {
                    return Status.COMPLETED;
                }
            } else if (!hasObjectiveAssessment && hasPerformanceAssessment) {
                if (perStatus.equals(Status.PASSED)) {
                    return Status.COMPLETED;
                }
            }
        }

        // if jun 30 < today or = today, return in-progress
        // if today > jun 30, reurn incomplete
        else if (termEndDate.isAfter(now) || termEndDate.isEqual(now)) {
            return Status.IN_PROGRESS;
        }

        //if you get here, the course is incomplete
        return Status.INCOMPLETE;

    }


    /**
     * Handles the save event
     */
    private void saveCourse()
    {
        Course course = createCourseObj();
        if (course == null) {

            //TODO: replace with AlertDialog??
            Toast.makeText(this, "Message not saved...", Toast.LENGTH_LONG).show();
            return;
        }
        Intent data = new Intent();


        if (course.getCourseId() != 0)
            data.putExtra(CoursesActivity.EXTRA_ID, courseId);
        data.putExtra(CoursesActivity.EXTRA_TITLE, course.getTitle());
        data.putExtra(CoursesActivity.EXTRA_START, course.getStartDate().toEpochDay());
        data.putExtra(CoursesActivity.EXTRA_END, course.getEndDate().toEpochDay());
        data.putExtra(CoursesActivity.EXTRA_STATUS, course.getStatus());
        data.putExtra(CoursesActivity.EXTRA_TERMID, course.getTermId());
        data.putExtra(CoursesActivity.EXTRA_MENTORID, course.getCourseMentorId());

        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Handles the View Mentor click event and loads a popup window
     * displaying the mentor's contact information.
     *
     * @param view
     */
    private void onButtonViewMentorContactClick(View view)
    {
        if (courseId == 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage("You must save the course first before you can see the assigned mentor")
                    .setIcon(R.drawable.ic_incomplete_72dp)
                    .setNeutralButton(R.string.ok, null)
                    .show();
            return;
        }

        LayoutInflater inflater = (LayoutInflater) CourseAddEditActivity.this.getSystemService(
                LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.layout_contact_mentor, null);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(
                popupView, width, height, focusable
        );

        TextView mentorName = popupView.findViewById(R.id.text_view_mentor_name);
        TextView mentorPhone = popupView.findViewById(R.id.text_view_mentor_phone);
        TextView mentorEmail = popupView.findViewById(R.id.text_view_mentor_email);


        MentorRepository repository = new MentorRepository(getApplication(), courseId);
        Mentor mentor = repository.getMentor();

        mentorName.setText(mentor.getName());
        mentorPhone.setText(Utils.formatPhoneNumber(mentor.getPhoneNumber()));
        mentorEmail.setText(mentor.getEmail());

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(
                new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        popupWindow.dismiss();
                        return true;
                    }
                });

    }

    /**
     * Handles the Select a Mentor click event and calls the Select Mentor activity
     */
    private void onButtonSelectAMentorClick()
    {
        Intent intent = new Intent(CourseAddEditActivity.this, SelectMentorActivity.class);
        startActivityForResult(intent, SELECT_MENTOR_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK)
            return;

        if (requestCode == SELECT_MENTOR_REQUEST) {
            this.mentorId = data.getIntExtra(SelectMentorActivity.EXTRA_MENTOR_ID, 0);
            contactCourseMentor.setText("Contact Course Mentor");
        }
        if (requestCode == VIEW_NOTES_REQUEST) {
//            courseId = data.getIntExtra(NoteAddEditActivity.EXTRA_COURSE_ID, 0);
            return;
        }

        Assessment assessment = new Assessment();

        assessment.setAssessmentCode(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE));
        assessment.setAssessmentType(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_TYPE));
        assessment.setResult(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT));
        assessment.setCourseId(courseId);
        assessment.setAssessmentDate(LocalDate.ofEpochDay(data.getLongExtra(
                CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED,
                0)));

        if (requestCode == ADD_ASSESSMENT_REQUEST) {
            assessmentViewModel.insertAssessment(assessment);
        }

        if (requestCode == EDIT_ASSESSMENT_REQUEST) {
            assessment.setAssessmentId(data.getIntExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, 0));
            assessmentViewModel.updateAssessment(assessment);
        }

        if (requestCode == DELETE_ASSESSMENT_REQUEST) {
            assessment.setAssessmentId(data.getIntExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, 0));
            assessmentViewModel.deleteAssessment(assessment);
        }


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
        menuInflater.inflate(R.menu.course_add_edit_menu, menu);
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
            case R.id.go_to_assessments:
                intentFromCaller = new Intent(this, AssessmentsActivity.class);
                startActivity(intentFromCaller);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_course_reminder:
                if (courseId == 0) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.course_reminder_error)
                            .setIcon(R.drawable.ic_incomplete_72dp)
                            .setNeutralButton(R.string.ok, null)
                            .show();
                    return false;
                }
                intent = new Intent(this, CourseAlertActivity.class);
                intent.putExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_NAME,
                        editTextCourseTitle.getText().toString());
                intent.putExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_START,
                        Utils.convertStringDate(editTextCourseStart.getText().toString()).toEpochDay());
                intent.putExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_END,
                        Utils.convertStringDate(editTextCourseEnd.getText().toString()).toEpochDay());

                startActivityForResult(intent, ADD_COURSE_ALERT_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
