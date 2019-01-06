package com.termtacker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.termtacker.R;
import com.termtacker.data.MentorRepository;
import com.termtacker.models.Mentor;
import com.termtacker.utilities.CourseViewModel;

import androidx.appcompat.app.AppCompatActivity;

public class NoteAddEditActivity extends AppCompatActivity
{
    private TextView textViewCourseName;
    private EditText editTextCourseNotes;
    private Button buttonSave;
    private Button buttonShare;

    private int courseId;
    public static final String EXTRA_COURSE_ID = "com.termtracker.NoteAddEditActivity_EXTRA_COURSE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setTitle("Course Notes");
        getUIFieldReferences();
        loadUIFieldValues();
        setupButtonListeners();
    }


    /**
     * Sets up the button listeners for notes
     */
    private void setupButtonListeners()
    {
        buttonSave.setOnClickListener(listener -> {

            Intent intent = new Intent();
            String note = editTextCourseNotes.getText().toString();

            if (note == null || note.isEmpty()) {

                setResult(RESULT_OK);
            } else {

                CourseViewModel cvm = new CourseViewModel(getApplication(),
                        courseId, false);
                cvm.updateCourseNotes(courseId, note);
                intent.putExtra(EXTRA_COURSE_ID, courseId);
                setResult(RESULT_OK, intent);
            }

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


    /**
     * Binds UI elements to their respective IDs
     */
    private void getUIFieldReferences()
    {
        textViewCourseName = findViewById(R.id.notes_coursename);
        editTextCourseNotes = findViewById(R.id.notes_notetext);
        buttonSave = findViewById(R.id.notes_buttonsave);
        buttonShare = findViewById(R.id.notes_buttonshare);
    }


    /**
     * Loads UI elements with values
     */
    private void loadUIFieldValues()
    {
        Intent intent = getIntent();
        if (intent.hasExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_ID)) {
            courseId = intent.getIntExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_ID, 0);
            textViewCourseName.setText(intent.getStringExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NAME));

            if (intent.hasExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NOTETEXT))
                editTextCourseNotes.setText(
                        intent.getStringExtra(CourseAddEditActivity.EXTRA_NOTES_COURSE_NOTETEXT));
        }
    }
}
