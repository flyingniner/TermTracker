package com.termtacker.data;

import com.termtacker.models.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao
{
    @Insert()
    long insertNote(Note note);

    @Update()
    int updateNote(Note... note);

    @Query("SELECT * FROM NOTES WHERE FK_COURSE_ID = :courseId")
    LiveData<List<Note>> getCourseNotesByCourseId(int courseId);

    @Delete()
    int deleteNote(Note... notes);
}
