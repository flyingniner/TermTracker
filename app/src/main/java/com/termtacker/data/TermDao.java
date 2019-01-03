package com.termtacker.data;

import com.termtacker.models.Term;

import java.time.LocalDate;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TermDao
{
    @Insert
    void insertTerm(Term... term);

    @Query("SELECT * FROM TERMS WHERE TERM_ID = :termId")
    Term getTermById(int termId);

    @Query("SELECT * FROM TERMS")
    LiveData<List<Term>> getTerms();

    @Update
    int updateTerm(Term term);

    @Delete
    int deleteTerm(Term term);

    @Query("SELECT COUNT(*) FROM TERMS")
    int countTerms();

    @Query("SELECT TERM_START_DATE FROM TERMS WHERE TERM_ID = :termId")
    LocalDate getTermStartDate(int termId);

    @Query("SELECT TERM_END_DATE FROM TERMS WHERE TERM_ID = :termId")
    LocalDate getTermEndDate(int termId);
}
