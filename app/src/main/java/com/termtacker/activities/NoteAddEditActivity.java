package com.termtacker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.termtacker.R;
import com.termtacker.data.MentorRepository;
import com.termtacker.models.Mentor;
import com.termtacker.utilities.CourseViewModel;
import com.termtacker.utilities.CoursesViewModelFactory;
import com.termtacker.utilities.NoteViewModel;
import com.termtacker.utilities.Utils;

import java.time.LocalDate;

public class NoteAddEditActivity extends AppCompatActivity
{

//    public static final int ADD_NOTES_REQUEST = 22;
//    public static final int EDIT_NOTES_REQUEST = 24;
//    private ShareActionProvider shareActionProvider;
//
//    public static final String EXTRA_ID = "com.termtracker.NotesActivity.EXTRA_ID";
//    public static final String EXTRA_DATE = "com.termtracker.NotesActivity.EXTRA_DATE";
//    public static final String EXTRA_NOTE_TEXT = "com.termtracker.NotesActivity.EXTRA_NOTE_TEXT";
//    public static final String EXTRA_COURSE_ID = "com.termtracker.NotesActivity.EXTRA_ASSESSMENT_COURSE_ID";
//    public static final String EXTRA_COURSE_NAME = "com.termtracker.NotesActivity.EXTRA_ASSESSMENT_COURSE_NAME";

    private TextView textViewCourseName;
    private EditText editTextCourseNotes;
    private Button buttonSave;
    private Button buttonShare;

    private int courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        getUIFieldReferences();
        loadUIFieldValues();
        setupButtonListeners();

    }

    private void setupButtonListeners()
    {
        buttonSave.setOnClickListener(listener -> {
            CourseViewModel cvm = new CourseViewModel(getApplication(), courseId, false);
            cvm.updateCourseNotes(courseId, editTextCourseNotes.getText().toString());
            setResult(RESULT_OK);
            finish();
        });

        buttonShare.setOnClickListener(listener -> {
            //TODO: Set up sharing

            MentorRepository repository = new MentorRepository(getApplication(), courseId);
            Mentor mentor = repository.getMentor();


            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + mentor.getEmail()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, textViewCourseName.getText().toString() +
            " Course Notes");
            emailIntent.putExtra(Intent.EXTRA_TEXT, editTextCourseNotes.getText().toString());

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }

        });
    }


    private void getUIFieldReferences()
    {
        textViewCourseName = findViewById(R.id.notes_coursename);
        editTextCourseNotes = findViewById(R.id.notes_notetext);
        buttonSave = findViewById(R.id.notes_buttonsave);
        buttonShare = findViewById(R.id.notes_buttonshare);
    }


    private void loadUIFieldValues()
    {
        Intent intent = getIntent();
        if (intent.hasExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_ID))
        {
            courseId = intent.getIntExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_ID, 0);
            textViewCourseName.setText(intent.getStringExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NAME));

            if (intent.hasExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NOTETEXT))
                editTextCourseNotes.setText(
                        intent.getStringExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NOTETEXT));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}
