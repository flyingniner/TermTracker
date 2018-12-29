package com.termtacker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
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

import com.termtacker.models.AssessmentAdapter;
import com.termtacker.data.MentorRepository;
import com.termtacker.R;
import com.termtacker.models.Status;
import com.termtacker.models.Assessment;
import com.termtacker.models.Course;
import com.termtacker.models.Mentor;
import com.termtacker.utilities.AssessmentViewModel;
import com.termtacker.utilities.CourseViewModel;
import com.termtacker.utilities.Utils;

import java.time.LocalDate;

public class CourseAddEditActivity extends AppCompatActivity
{
    private static final String TAG = TermAddEditActivity.class.getCanonicalName();
    //region requests
    public static final int SELECT_MENTOR_REQUEST = 7;
    public static final int VIEW_NOTES_REQUEST = 23;
    private static final int ADD_ASSESSMENT_REQUEST = 31;
    public static final int EDIT_ASSESSMENT_REQUEST = 32;
    public static final int ADD_COURSE_ALERT_REQUEST = 34;
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
    public static final String EXTRA_ALERT_COURSE_ID =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_ID";
    public static final String EXTRA_ALERT_COURSE_NAME =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_NAME";
    public static final String EXTRA_ALERT_COURSE_START =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_START";
    public static final String EXTRA_ALERT_COURSE_END =
            "com.termtracker.CourseAddEditActivity_EXTRA_ALERT_COURSE_END";

    //endregion
    //region Layout items
    private EditText editTextCourseTitle;
    private EditText editTextCourseStart;
    private TextView textViewCourseEndLabel;
    private EditText editTextCourseEnd;
    private ImageView startDatePicker;
    private ImageView endDatePicker;
    private EditText editTextCourseStatus;
//    private EditText editTextCourseTermId;
    private Button contactCourseMentor;
    private Button saveButton;
    private Button assessmentsButton;
    private Button addNoteButton;
    //endregion

    private Course course;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private CourseViewModel courseViewModel;
    private AssessmentViewModel assessmentViewModel;

    private boolean isExistingCourse = false;
    private int courseId;
    private int mentorId;
    private int termId;
    private LocalDate termEndDate;
    private Intent intentFromCaller;
    private boolean isCoursesActivityCaller = false;
    private boolean isTermAddEditActivityCaller = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        //region set form element references
        editTextCourseTitle = findViewById(R.id.course_add_edit_title);
        editTextCourseStart = findViewById(R.id.course_add_edit_start);
        textViewCourseEndLabel = findViewById(R.id.course_add_edit_end_label);
        editTextCourseEnd = findViewById(R.id.course_add_edit_end);
        startDatePicker = findViewById(R.id.course_add_edit_start_date_picker);
        endDatePicker = findViewById(R.id.course_add_edit_end_date_picker);
        contactCourseMentor = findViewById(R.id.course_add_edit_mentor);
        saveButton = findViewById(R.id.course_add_edit_save);
        assessmentsButton = findViewById(R.id.course_add_edit_add_assessement); //TODO: set listener + intent puts
        addNoteButton = findViewById(R.id.course_add_edit_notes); //TODO: set listener + intent puts
        //endregion

        //region set element values
        intentFromCaller = getIntent();

        isCoursesActivityCaller = intentFromCaller.hasExtra(CoursesActivity.EXTRA_ID) ? true : false;
        isTermAddEditActivityCaller = intentFromCaller.hasExtra(TermAddEditActivity.EXTRA_TERM_ID) ? true : false;

        loadExistingCourseDetails(intentFromCaller);



        //endregion

        //region set assessment(s) recycler view
        RecyclerView assessmentsRecyclerView = findViewById(R.id.course_add_edit_recycler_view);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRecyclerView.setHasFixedSize(true);

        AssessmentAdapter assessmentAdapter = new AssessmentAdapter();
        assessmentsRecyclerView.setAdapter(assessmentAdapter);

        assessmentViewModel = ViewModelProviders.of(this).get(AssessmentViewModel.class);
        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);

        if (courseId > 0)
            assessmentViewModel.getAssessmentsForCourse(courseId)
                    .observe(this, list -> assessmentAdapter.submitList(list));

        assessmentAdapter.setOnItemClickListener(assessment -> {
            Intent assessmentDataIntent = new Intent(CourseAddEditActivity.this,
                    AssessmentAddEditActivity.class);
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_ID, assessment.getAssessmentId());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_CODE, assessment.getAssessmentCode());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_TYPE, assessment.getAssessmentType());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_SCHEDULED, assessment.getAssessmentDate()
                    .format(Utils.dateFormatter_MMddyyyy));
            courseId = assessment.getCourseId();
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_COURSE_ID, courseId);
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_RESULT, assessment.getResult());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_COURSE_NAME, courseViewModel.getCourseName(courseId));

            startActivityForResult(assessmentDataIntent, EDIT_ASSESSMENT_REQUEST);
        });

        //endregion

        setButtonListeners();

    }

    /**
     * Prefills the form elements with the existing course details and
     * sets some global fields used in other methods
     *
     * @param intent
     */
    private void loadExistingCourseDetails(Intent intent)
    {
        if (isCoursesActivityCaller)
        {
            setTitle("Edit Course");
            isExistingCourse = true;
            courseId = intent.getIntExtra(CoursesActivity.EXTRA_ID, 0);
            mentorId = intent.getIntExtra(CoursesActivity.EXTRA_MENTORID, 0);
            termId = intent.getIntExtra(CoursesActivity.EXTRA_TERMID,0);

            editTextCourseTitle.setText(intent.getStringExtra(CoursesActivity.EXTRA_TITLE));
                editTextCourseStart.setText(LocalDate.ofEpochDay(
                        intent.getLongExtra(CoursesActivity.EXTRA_START, 0))
                        .format(Utils.dateFormatter_MMddyyyy));
                editTextCourseEnd.setText(LocalDate.ofEpochDay(
                        intent.getLongExtra(CoursesActivity.EXTRA_END, 0))
                        .format(Utils.dateFormatter_MMddyyyy));
            contactCourseMentor.setText("Contact Course Mentor");

            String status = intent.getStringExtra(CoursesActivity.EXTRA_STATUS);
            if (status == null)
                textViewCourseEndLabel.setText("Goal");
            else
                textViewCourseEndLabel.setText("Completed");

            termEndDate = LocalDate.ofEpochDay(intent.getLongExtra(CoursesActivity.EXTRA_TERM_END, 0));
        }
        else if (isTermAddEditActivityCaller)
        {
            if (intent.getIntExtra(TermAddEditActivity.EXTRA_COURSE_ID, 0) > 0)
            {
                setTitle("Edit Course");
                isExistingCourse = true;

                courseId = intent.getIntExtra(TermAddEditActivity.EXTRA_COURSE_ID, 0);
                mentorId = intent.getIntExtra(TermAddEditActivity.EXTRA_MENTOR_ID, 0);
                termId = intent.getIntExtra(TermAddEditActivity.EXTRA_TERM_ID,0);

                editTextCourseTitle.setText(intent.getStringExtra(TermAddEditActivity.EXTRA_TITLE));
                editTextCourseStart.setText(LocalDate.ofEpochDay(
                        intent.getLongExtra(TermAddEditActivity.EXTRA_START, 0))
                    .format(Utils.dateFormatter_MMddyyyy));
                editTextCourseEnd.setText(LocalDate.ofEpochDay(
                        intent.getLongExtra(TermAddEditActivity.EXTRA_COURSE_END, 0))
                        .format(Utils.dateFormatter_MMddyyyy));
                contactCourseMentor.setText("Contact Course Mentor");

                String status = intent.getStringExtra(TermAddEditActivity.EXTRA_STATUS);
                if (status == null)
                    textViewCourseEndLabel.setText("Goal");
                else
                    textViewCourseEndLabel.setText("Completed");

            }

            else
            {
                setTitle("Add Course");
                contactCourseMentor.setText("Select a Mentor");
            }

            termEndDate = LocalDate.ofEpochDay(intent.getLongExtra(
                    TermAddEditActivity.EXTRA_TERM_END, 0));
        }
    }

    /**
     * Creates the listeners the the date pickers, save button,
     * assessments, and notes buttons
     * and
     */
    private void setButtonListeners()
    {
        //region StartDate DatePicker
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

        //region EndDate DatePicker
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

        saveButton.setOnClickListener(listener ->
        {
            saveCourse();
        });

        assessmentsButton.setOnClickListener(listener -> {

            if (isExistingCourse) {
                Intent intent = new Intent(CourseAddEditActivity.this, AssessmentAddEditActivity.class);

                //TODO: try to add a new assessment to Intro to Programming, check
                //that the courseId is getting added.
                intent.putExtra(EXTRA_ASSESSMENT_COURSE_ID, courseId);

                startActivityForResult(intent, ADD_ASSESSMENT_REQUEST);
            }
            else
            {
                Toast.makeText(this,
                        "Please save the course before adding assessments.",
                        Toast.LENGTH_LONG).show();
                return;
            }
        });


        contactCourseMentor.setOnClickListener(listener -> {

            if (contactCourseMentor.getText().toString().equals("Select a Mentor"))
                onButtonSelectAMentorClick();
            else
                onButtonViewMentorContactClick(new View(this));
        });


        Button alertsButton = new Button(this);
        alertsButton.setOnClickListener(listener -> {
            //TODO: open alerts activity
            Intent intent = new Intent(CourseAddEditActivity.this, CourseAlertActivity.class);
            intent.putExtra(EXTRA_ALERT_COURSE_ID, courseId);
            intent.putExtra(EXTRA_ALERT_COURSE_START,
                    Utils.convertStringDate(editTextCourseStart.getText().toString()).toEpochDay());
            intent.putExtra(EXTRA_ALERT_COURSE_END,
                    Utils.convertStringDate(editTextCourseEnd.getText().toString()).toEpochDay());

            startActivityForResult(intent, ADD_COURSE_ALERT_REQUEST);
        });

    }

    /**
     * Creates a Course object and validates user input and provides
     * the user with feedback if a field does not meet input requirements.
     * @return
     */
    private Course createCourseObj()
    {
        Course c = new Course();
        String title = editTextCourseTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "A title is required", Toast.LENGTH_LONG).show();
            return null;
        }

        String startStr = editTextCourseStart.getText().toString();
        if (startStr.isEmpty()) {
            Toast.makeText(this, "A start date is required", Toast.LENGTH_LONG).show();
            return null;
        }
        String[] s = startStr.split("/");
        LocalDate start = LocalDate.of(Integer.parseInt(s[2]), Integer.parseInt(s[0]), Integer
                .parseInt(s[1]));


        String endStr = editTextCourseEnd.getText().toString();
        if (endStr.isEmpty()) {
            Toast.makeText(this, "An end date is required", Toast.LENGTH_LONG).show();
            return null;
        }
        String[] e = endStr.split("/");
        LocalDate end = LocalDate.of(Integer.parseInt(e[2]), Integer.parseInt(e[0]), Integer
                .parseInt(e[1]));

        if(!checkDatesAreAcceptable(start, end, termEndDate))
            return null;


//        if (start.isAfter(end)) {
//            Toast.makeText(this, "Term Start must come before End", Toast.LENGTH_LONG).show();
//            return null;
//        }

//        int termId = Integer.parseInt(editTextCourseTermId.getText().toString());


//        String status = editTextCourseStatus.getText().toString();
        String status = determineCourseStatus();

        c.setTitle(title);
        c.setTermId(termId);
        c.setStartDate(start);
        c.setEndDate(end);
        c.setStatus(status);
        c.setCourseMentorId(mentorId);

        if (isExistingCourse)
        {
            c.setCourseId(courseId);
//            c.setTermId(termId);
        }

        return c;
    }

    /**
     * verifies the course start and end dates are valid for the term and
     * that the end date does not come before the start date.
     * @param start
     * @param end
     * @param termEndDate
     * @return
     */
    private boolean checkDatesAreAcceptable(LocalDate start, LocalDate end, LocalDate termEndDate)
    {
        if (end.isAfter(termEndDate))
        {
            Toast.makeText(this,
                    "The ending date for the course must be less than term end date.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        //TODO: check the start date is not after end date,
        if (end.isBefore(start))
        {
            Toast.makeText(this,
                    "The ending date must come on or before the course end date",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        //TODO: check the start date is not before term start
//        if (true)
//        {
//            return false;
//        }

        return true;
    }

    /**
     * Determines the status of the course based on the result of the
     * associated assessments. If no assessments present, the status will
     * show pending. To show as passed, both Objective and Performance
     * assessments must have at least one assessment marked as passed.
     * @return
     */
    private String determineCourseStatus()
    {
        LocalDate now = LocalDate.now();

        String objStatus = "";
        String perStatus = "";
        boolean hasObjectiveAssessment = false;
        boolean hasPerformanceAssessment = false;

        if (courseId > 0)
        {
            for (Assessment a : assessmentViewModel.getNonObservableAssessmentsForCourse(courseId))
            {
                if (a.getAssessmentType().equals("Objective"))
                {
                    hasObjectiveAssessment = true;
                    if (a.getResult().equals(Status.PASSED))
                    {
                        objStatus = Status.PASSED;
                    }
                }
                if (a.getAssessmentType().equals("Performance"))
                {
                    hasPerformanceAssessment = true;
                    if (a.getResult().equals(Status.PASSED))
                    {
                        perStatus = Status.PASSED;
                    }
                }
            }
        }

        if (hasObjectiveAssessment && hasPerformanceAssessment)
        {
            if (objStatus.equals(Status.PASSED) && perStatus.equals(Status.PASSED))
            {
                return Status.COMPLETED;
            }
        }

        else if (hasObjectiveAssessment && !hasPerformanceAssessment)
        {
            if (objStatus.equals(Status.PASSED))
            {
                return Status.COMPLETED;
            }
        }

        else if (!hasObjectiveAssessment && hasPerformanceAssessment)
        {
            if (perStatus.equals(Status.PASSED))
            {
                return Status.COMPLETED;
            }
        }

        // if jun 30 < today or = today, return in-progress
        // if today > jun 30, reurn incomplete
        else if (termEndDate.isAfter(now) || termEndDate.isEqual(now))
        {
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
        if (course == null)
        {
            Toast.makeText(this, "Message not saved...", Toast.LENGTH_LONG).show();
            return;
        }
        Intent data = new Intent();

        if (isCoursesActivityCaller)
        {
            if (course.getCourseId() != 0)
                data.putExtra(CoursesActivity.EXTRA_ID, courseId);
            data.putExtra(CoursesActivity.EXTRA_TITLE, course.getTitle());
            data.putExtra(CoursesActivity.EXTRA_START, course.getStartDate().toEpochDay());
            data.putExtra(CoursesActivity.EXTRA_END, course.getEndDate().toEpochDay());
            data.putExtra(CoursesActivity.EXTRA_STATUS, course.getStatus());
            data.putExtra(CoursesActivity.EXTRA_TERMID, course.getTermId());
            data.putExtra(CoursesActivity.EXTRA_MENTORID, course.getCourseMentorId());
        }
        else
        {
            if (course.getCourseId() != 0)
                data.putExtra(TermAddEditActivity.EXTRA_COURSE_ID, courseId);
            data.putExtra(TermAddEditActivity.EXTRA_TITLE, course.getTitle());
            data.putExtra(TermAddEditActivity.EXTRA_START, course.getStartDate().toEpochDay());
            data.putExtra(TermAddEditActivity.EXTRA_COURSE_END, course.getEndDate().toEpochDay());
            data.putExtra(TermAddEditActivity.EXTRA_STATUS, course.getStatus());
            data.putExtra(TermAddEditActivity.EXTRA_TERM_ID, course.getTermId());
            data.putExtra(TermAddEditActivity.EXTRA_MENTOR_ID, course.getCourseMentorId());
        }

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
            Toast.makeText(this,
                    "You must save the course first before you can see the assigned mentor",
                    Toast.LENGTH_SHORT).show();
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

        if (requestCode == SELECT_MENTOR_REQUEST)
        {
            this.mentorId = data.getIntExtra(SelectMentorActivity.EXTRA_MENTOR_ID, 0);
            contactCourseMentor.setText("Contact Course Mentor");
        }

        Assessment assessment = new Assessment();

        assessment.setAssessmentCode(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_CODE));
        assessment.setAssessmentType(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_TYPE));
        assessment.setResult(data.getStringExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_RESULT));
        assessment.setCourseId(courseId);
        assessment.setAssessmentDate(LocalDate.ofEpochDay(data.getLongExtra(
                CourseAddEditActivity.EXTRA_ASSESSMENT_SCHEDULED,
                0)));

        if (requestCode == ADD_ASSESSMENT_REQUEST)
        {
            assessmentViewModel.insertAssessment(assessment);
        }

        if (requestCode == EDIT_ASSESSMENT_REQUEST)
        {
            assessment.setAssessmentId(data.getIntExtra(CourseAddEditActivity.EXTRA_ASSESSMENT_ID, 0));
            assessmentViewModel.updateAssessment(assessment);
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
        menuInflater.inflate(R.menu.course_menu, menu);
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
//                intent = new Intent(this, AssessmentsActivity.class);
//                startActivityForResult(intent,0);
//          saveCourse();
                return true;
            case R.id.go_to_mentors:
//                intent = new Intent(this, MentorActivity.class);
//                startActivityForResult(intent, 0)
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_course_reminder:
                intent = new Intent(this, CourseAlertActivity.class);
                intent.putExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_NAME, editTextCourseTitle.getText().toString());
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
