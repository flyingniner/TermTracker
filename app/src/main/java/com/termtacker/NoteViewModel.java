package com.termtacker;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NoteViewModel extends AndroidViewModel
{
    private NoteRepository repository;
    private LiveData<List<Note>> notes;

    public NoteViewModel(@NonNull Application application, int courseID)
    {
        super(application);
        repository = new NoteRepository(application, courseID);
        notes = repository.getNotesForCourse();
    }

    public void insertNote(Note note)
    {
        repository.insertNote(note);
    }

    public void updateNote(Note note)
    {
        repository.updateNote(note);
    }

    public void deleteNote(Note note)
    {
        repository.deleteNote(note);
    }

    public LiveData<List<Note>>getCourseNotes()
    {
        return notes;
    }
}
