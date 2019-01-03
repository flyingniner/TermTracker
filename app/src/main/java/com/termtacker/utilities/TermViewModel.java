package com.termtacker.utilities;

import android.app.Application;

import com.termtacker.data.TermRepository;
import com.termtacker.models.Term;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TermViewModel extends AndroidViewModel
{
    private TermRepository repository;
    private LiveData<List<Term>> allterms;

    public TermViewModel(@NonNull Application application)
    {
        super(application);
        repository = new TermRepository(application);
        allterms = repository.getTerms();
    }

    public void insertTerm(Term term)
    {
        repository.insertTerm(term);
    }

    public void updateTerm(Term term)
    {
        repository.updateTerm(term);
    }

    public void deleteTerm(Term term)
    {
        repository.deleteTerm(term);
    }

    public LiveData<List<Term>> getAllterms()
    {
        return allterms;
    }

    public LocalDate getTermEndDate(int termId) { return repository.getTermEndDate(termId);}
}
