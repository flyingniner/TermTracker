package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;

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

    private int termId;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private CourseViewModel courseViewModel;
    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add_edit);


        Intent intent = getIntent();

        termId = intent.getIntExtra(EXTRA_CURRENT_TERM_ID, -1);
        editTextCourseTitle = findViewById(R.id.course_add_edit_title);
        editTextCourseStart = findViewById(R.id.course_add_edit_start);
        textViewCourseEndLabel = findViewById(R.id.course_add_edit_end_label);
        editTextCourseEnd = findViewById(R.id.course_add_edit_end);
        startDatePicker = findViewById(R.id.course_add_edit_start_date_picker);
        endDatePicker = findViewById(R.id.course_add_edit_end_date_picker);
        saveButton = findViewById(R.id.course_add_edit_save);
        addAssessmentsButton = findViewById(R.id.course_add_edit_add_assessement);
        addNoteButton = findViewById(R.id.course_add_edit_notes);

        if (intent.hasExtra(CoursesActivity.EXTRA_ID))  //not sure if this the correct id to look
        // for, maybe use the term id?
        {
            setTitle("Edit Course");

            editTextCourseTitle.setText(intent.getStringExtra(CoursesActivity.EXTRA_TITLE).toString());
            editTextCourseStart.setText(intent.getStringExtra(CoursesActivity.EXTRA_START).toString());

            String endDateStr = intent.getStringExtra(CoursesActivity.EXTRA_END).toString();
            editTextCourseEnd.setText(endDateStr);

            String[] endDateArry = endDateStr.split("/");

            LocalDate endDate = LocalDate.of(Integer.parseInt(endDateArry[2]),
                                             Integer.parseInt(endDateArry[0]),
                                             Integer.parseInt(endDateArry[1]));

            if (intent.getStringExtra(CoursesActivity.EXTRA_STATUS).toString() == Status.COMPLETED)
                textViewCourseEndLabel.setText("Completed");
            else
                textViewCourseEndLabel.setText("Goal");

        }
        else
        {
            setTitle("Add Course");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        /**
         * This section here is for displaying the sublist of assessments associated with the
         * course.
         * At course creation, no assessments will be present.
         RecyclerView recyclerView = findViewById(R.id.course_add_edit_recycler_view); //this
         displays assessments
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setHasFixedSize(true);

         //TODO: uncomment after assessment adpater is created
         //        final AssessementAdapter assessementAdapter = new CourseAdapter();
         //        recyclerView.setAdapter(assessementAdapter);

         final CourseAdapter courseAdapter = new CourseAdapter();

         courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
         courseViewModel.getFilteredCourses(termId).observe(this, courses -> courseAdapter
         .submitList(courses));

         courseAdapter.setOnItemClickListener(new CourseAdapter.onItemClickListener()
         {
         @Override public void onItemClick(Course course)
         {

         }
         });
         */


        setButtonListeners();

    }

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
                                                (monthOfYear + 1) + "/" +dayOfMonth + "/"+ year);
                                    }
                                }, year, month, day);

                datePickerDialog.show();
            }
        });
        //endregion


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

        addAssessmentsButton.setOnClickListener(listener ->
        {
//
////            Intent intent = new Intent(this, AssessmentActivity.class);
////            intent.putExtra(EXTRA_CURRENT_TERM_ID,
////                            getIntent().getIntExtra
////                                    (TermActivity.EXTRA_ID,
////                                                                           -1
//            ));

            //TODO: save the course and then launch the assessments activity. Not usre if I can
            // use my already created "saveTerm()" because that sends a request item back to
            // the calling activity, so probably need to overload or call Term

            //basically, will need to query the db to see if this term exists, and if so, then
            // peform an update. Else, insert. eitherway, I'll need to capture the ID from
            // teh insert/update statement to pass along as an intent to the next screen.

            //alternatively, i could save the term, then update the buttons dynamially: Save
            // becomes "close", which just returns to teh prior screen, but the add courses still
            // will be available? not sure yet.
        });
    }

    private void saveCourse()
    {
        String title = editTextCourseTitle.getText().toString().trim();
        if (title.isEmpty())
        {
            Toast.makeText(this, "A title is required", Toast.LENGTH_LONG);
            return;
        }

        String startStr = editTextCourseStart.getText().toString();
        if (startStr.isEmpty())
        {
            Toast.makeText(this, "A start date is required", Toast.LENGTH_LONG);
            return;
        }
        String[] s = startStr.split("/");
        LocalDate start = LocalDate.of(Integer.parseInt(s[2]), Integer.parseInt(s[0]), Integer
                .parseInt(s[1]));


        String endStr = editTextCourseEnd.getText().toString();
        if (endStr.isEmpty())
        {
            Toast.makeText(this, "An end date is required", Toast.LENGTH_LONG);
            return;
        }
        String[] e = endStr.split("/");
        LocalDate end = LocalDate.of(Integer.parseInt(e[2]), Integer.parseInt(e[0]), Integer
                .parseInt(e[1]));

        if (start.isAfter(end))
        {
            Toast.makeText(this, "Term Start must come before End", Toast.LENGTH_LONG);
            return;
        }

        Intent data = new Intent();
        data.putExtra(CoursesActivity.EXTRA_TITLE, title);
        data.putExtra(CoursesActivity.EXTRA_START, start.toEpochDay());
        data.putExtra(CoursesActivity.EXTRA_END, end.toEpochDay());

        int id = getIntent().getIntExtra(TermActivity.EXTRA_ID, -1);
        if (id != -1)
        {
            data.putExtra(TermActivity.EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
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
        switch (item.getItemId())
        {
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
