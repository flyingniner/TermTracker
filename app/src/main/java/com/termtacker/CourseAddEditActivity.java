package com.termtacker;

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

import java.time.LocalDate;

public class CourseAddEditActivity extends AppCompatActivity
{
    private static final String TAG = TermAddEditActivity.class.getCanonicalName();
    //region requests
    public static final int SELECT_MENTOR_REQUEST = 7;
    public static final int VIEW_NOTES_REQUEST = 23;
    private static final int ADD_ASSESSMENT_REQUEST = 31;
    public static final int EDIT_ASSESSMENT_REQUEST = 32;
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
    //endregion
    //region Layout items
    private EditText editTextCourseTitle;
    private EditText editTextCourseStart;
    private TextView textViewCourseEndLabel;
    private EditText editTextCourseEnd;
    private ImageView startDatePicker;
    private ImageView endDatePicker;
    private EditText editTextCourseStatus;
    private EditText editTextCourseTermId;
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
        Intent intent = getIntent();
        if (intent.hasExtra(CoursesActivity.EXTRA_ID)) {
            loadExistingCourseDetails(intent);
        } else {
            setTitle("Add Course");
            contactCourseMentor.setText("Select a Mentor");
        }
        //endregion

        //region set assessment(s) recycler view
        RecyclerView assessmentsRecyclerView = findViewById(R.id.course_add_edit_recycler_view);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRecyclerView.setHasFixedSize(true);

        AssessmentAdapter assessmentAdapter = new AssessmentAdapter();
        assessmentsRecyclerView.setAdapter(assessmentAdapter);

        assessmentViewModel = ViewModelProviders.of(this).get(AssessmentViewModel.class);

        if (courseId > 0)
            assessmentViewModel.getAssessmentsForCourse(courseId)
                    .observe(this, list -> assessmentAdapter.submitList(list));

        assessmentAdapter.setOnItemClickListener(assessment -> {
            Intent assessmentDataIntent = new Intent(CourseAddEditActivity.this,
                    AssessmentsActivity.class);
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_ID, assessment.getAssessmentId());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_CODE, assessment.getAssessmentCode());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_TYPE, assessment.getAssessmentType());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_SCHEDULED, assessment.getAssessmentDate()
                    .format(Utils.dateFormatter_MMddyyyy));
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_COURSE_ID, assessment.getCourseId());
            assessmentDataIntent.putExtra(EXTRA_ASSESSMENT_RESULT, assessment.getResult());

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
        setTitle("Edit Course");
        isExistingCourse = true;
        courseId = intent.getIntExtra(CoursesActivity.EXTRA_ID, 0);
        mentorId = intent.getIntExtra(CoursesActivity.EXTRA_MENTORID, 0);

        editTextCourseTitle.setText(intent.getStringExtra(CoursesActivity.EXTRA_TITLE));
        editTextCourseStart.setText(intent.getStringExtra(CoursesActivity.EXTRA_START));
        editTextCourseEnd.setText(intent.getStringExtra(CoursesActivity.EXTRA_END));
        contactCourseMentor.setText("Contact Course Mentor");

        String status = intent.getStringExtra(CoursesActivity.EXTRA_STATUS);
        if (status == null)
            textViewCourseEndLabel.setText("Goal");
        else
            textViewCourseEndLabel.setText("Completed");
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

                intent.putExtra(EXTRA_ASSESSMENT_COURSE_ID, courseId);
                startActivityForResult(intent, ADD_ASSESSMENT_REQUEST);
            } else {
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
    }

    /**
     * Creates a Course object and validates user input and provides
     * the user with feedback if a field does not meet input requirements.
     * @return
     */
    private Course createCourseObj()
    {
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

        if (start.isAfter(end)) {
            Toast.makeText(this, "Term Start must come before End", Toast.LENGTH_LONG).show();
            return null;
        }

        int termId = Integer.parseInt(editTextCourseTermId.getText().toString());


        String status = editTextCourseStatus.getText().toString();

        course.setTitle(title);
        course.setTermId(termId);
        course.setStartDate(start);
        course.setEndDate(end);
        course.setStatus(status);
        course.setCourseMentorId(mentorId);

        if (isExistingCourse)
            course.setCourseId(courseId);

        return course;
    }


    /**
     * Handles the save event
     */
    private void saveCourse()
    {
        Course course = createCourseObj();
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

        if (requestCode == SELECT_MENTOR_REQUEST) {
            this.mentorId = data.getIntExtra(SelectMentorActivity.EXTRA_MENTOR_ID, 0);
            contactCourseMentor.setText("Contact Course Mentor");
        }

        if (requestCode == ADD_ASSESSMENT_REQUEST) {
            Assessment assessment = new Assessment();
            assessment.setAssessmentDate(LocalDate.ofEpochDay(data.getLongExtra(
                    AssessmentsActivity.EXTRA_ASSESSMENT_SCHEDULED,
                    0)));

            assessment.setAssessmentType(data.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_TYPE));
            assessment.setAssessmentCode(data.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_CODE));

            assessment.setResult(data.getStringExtra(AssessmentsActivity.EXTRA_ASSESSMENT_RESULT));
            assessment.setCourseId(courseId);

            assessmentViewModel.insertAssessment(assessment);

        }

        if (requestCode == EDIT_ASSESSMENT_REQUEST) {
            //TODO
            Assessment assessment = new Assessment();
            assessment.setAssessmentId(data.getIntExtra(AssessmentsActivity.EXTRA_ID, 0));

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
        menuInflater.inflate(R.menu.term_menu, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
