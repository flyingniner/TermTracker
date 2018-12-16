package com.termtacker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ShareActionProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity
{
    public static final int ADD_NOTES_REQUEST = 22;
    public static final int EDIT_NOTES_REQUEST = 24;
    private ShareActionProvider shareActionProvider;
    private NoteViewModel noteViewModel;
    public static final String EXTRA_ID = "com.termtracker.NotesActivity.EXTRA_ID";
    public static final String EXTRA_DATE = "com.termtracker.NotesActivity.EXTRA_DATE";
    public static final String EXTRA_NOTE_TEXT = "com.termtracker.NotesActivity.EXTRA_NOTE_TEXT";
    public static final String EXTRA_COURSE_ID = "com.termtracker.NotesActivity.EXTRA_ASSESSMENT_COURSE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_black_24dp);

        FloatingActionButton floatingActionButton = findViewById(R.id.notes_add_note);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteAddEditActivity.class);
            startActivityForResult(intent, ADD_NOTES_REQUEST);
        });


        final NoteAdapter noteAdapter = new NoteAdapter();

        RecyclerView recyclerView = findViewById(R.id.notes_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getCourseNotes().observe(this, list -> noteAdapter.submitList(list));

        noteAdapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(this,NoteAddEditActivity.class);
            intent.putExtra(EXTRA_ID,note.getCourseId());
            intent.putExtra(EXTRA_DATE,note.getNoteDate().format(Utils.dateFormatter_MMddyyyy));
            intent.putExtra(EXTRA_NOTE_TEXT,note.getNote());
            intent.putExtra(EXTRA_COURSE_ID,note.getCourseId());

            startActivityForResult(intent, ADD_NOTES_REQUEST);
        });

        List<Note> shareNotesList = noteViewModel.getCourseNotes().getValue();


        //TODO: send notes to email...can this be done with a Bundle???
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//        sendIntent.setType("text/plain");
//
//        startActivity(sendIntent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Note note = new Note();
            note.setNoteDate(LocalDate.ofEpochDay(data.getIntExtra(EXTRA_DATE, 0)));
            note.setNote(data.getStringExtra(EXTRA_NOTE_TEXT));
            note.setCourseId(data.getIntExtra(EXTRA_COURSE_ID, 0));

            if (requestCode == EDIT_NOTES_REQUEST)
            {
                note.setCourseNoteId(data.getIntExtra(EXTRA_ID,0));
                noteViewModel.updateNote(note);
            }
            else
                noteViewModel.insertNote(note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.notes_share_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.share_notes);

        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();

        return true;
    }
}
