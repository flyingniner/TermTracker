package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.time.LocalDate;

public class TermAddEditActivity extends AppCompatActivity
{
    private EditText editTextTitle;
    private EditText editTextStart;
    private EditText editTextEnd;
    private ImageButton startDatePicker;
    private ImageButton endDatePicker;
    private Button buttonSave;
    private Button buttonAddCourse;

    private static final String TAG = TermAddEditActivity.class.getCanonicalName();
//    public static final String EXTRA_ID = "com.termtracker.EXTRA_ID";
//    public static final String EXTRA_TITLE = "com.termtracker.EXTRA_TITLE";
//    public static final String EXTRA_START = "com.termtracker.EXTRA_START";
//    public static final String EXTRA_END = "com.termtracker.EXTRA_END";
//    public static final String EXTRA_STATUS = "com.termtracker.EXTRA_STATUS";

    public static final String EXTRA_CURRENT_TERM_ID = "com.termtracker.EXTRA_CURRENT_TERM_ID";

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TermViewModel termViewModel;
    private CourseViewModel courseViewModel;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_add_edit);

        editTextTitle = findViewById(R.id.edit_term_title);
        editTextStart = findViewById(R.id.edit_term_start);
        editTextEnd = findViewById(R.id.edit_term_end);
        startDatePicker = findViewById(R.id.start_date_picker);
        endDatePicker = findViewById(R.id.end_date_picker);
        buttonSave = findViewById(R.id.save_term);
        buttonAddCourse = findViewById(R.id.add_course);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);


        //Check if this is an existing term that is to be edited
        Intent intent = getIntent();
        if (intent.hasExtra(TermActivity.EXTRA_ID))
        {
            setTitle("Edit Term");
            Log.d(TAG,"START: " + intent.getStringExtra(TermActivity.EXTRA_START));
            Log.d(TAG,"END: " + intent.getStringExtra(TermActivity.EXTRA_END));
            Log.d(TAG,"PROGRESS: " + intent.getStringExtra(TermActivity.EXTRA_STATUS));
            editTextTitle.setText(intent.getStringExtra(TermActivity.EXTRA_TITLE));
            editTextStart.setText(intent.getStringExtra(TermActivity.EXTRA_START));
            editTextEnd.setText(intent.getStringExtra(TermActivity.EXTRA_END));

            RecyclerView recyclerView = findViewById(R.id.edit_term_course_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);

            final CourseAdapter courseAdapter = new CourseAdapter();
            recyclerView.setAdapter(courseAdapter);

            courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
            courseViewModel.getFilteredCourses(intent.getIntExtra(TermActivity.EXTRA_ID,0))
                    .observe(this, list -> courseAdapter.submitList(list)
            );

        }
        else
        {
            setTitle("Add Term");
        }

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

                datePickerDialog = new DatePickerDialog(TermAddEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                editTextStart.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
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

                datePickerDialog = new DatePickerDialog(TermAddEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                editTextEnd.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        buttonSave.setOnClickListener(listener -> {
            saveTerm();
        });

        buttonAddCourse.setOnClickListener(listener -> {

            Intent intent = new Intent();
            intent.putExtra(EXTRA_CURRENT_TERM_ID, getIntent().getIntExtra(TermActivity.EXTRA_ID, -1));

            //TODO: save the term and then launch the courses activity. Not usre if I can use my already created "saveTerm()"
            //because that sends a request item back to the calling activity, so probably need to overload or call Term

            //basically, will need to query the db to see if this term exists, and if so, then peform an update. Else, insert.
            //eitherway, I'll need to capture the ID from teh insert/update statement to pass along as an intent to the next screen.

            //alternatively, i could save the term, then update the buttons dynamially: Save becomes "close", which just returns to teh
            //prior screen, but the add courses still will be available? not sure yet.
        });
    }

    private void saveTerm()
    {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty())
        {
            Toast.makeText(this,"A title is required",Toast.LENGTH_LONG);
            return;
        }

        String startStr = editTextStart.getText().toString();
        if (startStr.isEmpty())
        {
            Toast.makeText(this,"A start date is required",Toast.LENGTH_LONG);
            return;
        }
        String[] s = startStr.split("/");
        LocalDate start = LocalDate.of(Integer.parseInt(s[2]),Integer.parseInt(s[0]),Integer.parseInt(s[1]));


        String endStr = editTextEnd.getText().toString();
        if (endStr.isEmpty())
        {
            Toast.makeText(this,"An end date is required",Toast.LENGTH_LONG);
            return;
        }
        String[] e = endStr.split("/");
        LocalDate end = LocalDate.of(Integer.parseInt(e[2]),Integer.parseInt(e[0]),Integer.parseInt(e[1]));

        if(start.isAfter(end))
        {
            Toast.makeText(this,"Term Start must come before End",Toast.LENGTH_LONG);
            return;
        }

        Intent data = new Intent();
        data.putExtra(TermActivity.EXTRA_TITLE, title);
        data.putExtra(TermActivity.EXTRA_START, start.toEpochDay());
        data.putExtra(TermActivity.EXTRA_END, end.toEpochDay());

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
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.go_to_assessments:
//                intent = new Intent(this, AssessmentActivity.class);
//                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_courses:
                intent = new Intent(this, CoursesActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_mentors:
//                intent = new Intent(this, MentorsActivity.class);
//                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
