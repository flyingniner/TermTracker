package com.termtacker.data;

import android.app.Application;
import android.os.AsyncTask;

import com.termtacker.models.Note;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteRepository
{
    private LiveData<List<Note>> courseNotes;
    private NoteDao noteDao;

    public NoteRepository(Application application, int courseId)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        this.noteDao = database.noteDao();
        courseNotes = noteDao.getCourseNotesByCourseId(courseId);
    }

    //region Assessments
    public void insertNote(Note note)
    {
        new InsertNoteAsync(noteDao).execute(note);
    }

    public void updateNote(Note note)
    {
        new UpdateNoteAsync(noteDao).execute(note);
    }

    public void deleteNote(Note note)
    {
        new DeleteNoteAsync(noteDao).execute(note);
    }

    public LiveData<List<Note>> getNotesForCourse()
    {
        return courseNotes;
    }
//endregion

//region AsyncCalls
    private static class InsertNoteAsync extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private InsertNoteAsync(NoteDao noteDao)
        {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes)
        {
            noteDao.insertNote(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsync extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private UpdateNoteAsync(NoteDao noteDao)
        {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes)
        {
            noteDao.updateNote(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsync extends AsyncTask<Note, Void, Void>
    {
        private NoteDao noteDao;

        private DeleteNoteAsync(NoteDao noteDao)
        {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes)
        {
            noteDao.deleteNote(notes[0]);
            return null;
        }
    }
//endregion
}


