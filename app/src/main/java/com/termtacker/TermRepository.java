package com.termtacker;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;

public class TermRepository
{
    private LiveData<List<Term>> terms;
    private TermDao termDao;
    private CardView cardView;

    public TermRepository(Application application)
    {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());

        termDao = database.termDao();
        terms = termDao.getTerms();
        cardView = new CardView(application.getApplicationContext());
    }

    public void insertTerm(Term term)
    {
        new InsertTermAsync(termDao).execute(term);
    }

    public void updateTerm(Term term)
    {
        new UpdateTermAsync(termDao).execute(term);
    }

    public void deleteTerm(Term term)
    {
        new DeleteTermAsync(termDao).execute(term);
    }



    public LiveData<List<Term>> getTerms()
    {
        return terms;
    }

    public static class InsertTermAsync extends AsyncTask<Term, Void, Void>
    {
        private static final String TAG = "InsertTermAsync";

        private TermDao termDao;

        private InsertTermAsync(TermDao termDao)
        {
            this.termDao = termDao;

        }

        @Override
        protected Void doInBackground(Term... terms)
        {
            Log.d(TAG, "Background thread started");
            Log.d(TAG, "terms[] has X terms: " + terms.length);
            Log.d(TAG, "Attempting to insert term now");
            try
            {
                termDao.insertTerm(terms[0]);

            }
            catch (Exception e)
            {
                Log.d(TAG, "term failed to insert");
                throw e;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    public static class UpdateTermAsync extends AsyncTask<Term, Void, Void>
    {
        private TermDao termDao;

        public UpdateTermAsync(TermDao termDao)
        {
            this.termDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms)
        {
            termDao.updateTerm(terms[0]);
            return null;
        }
    }

    public static class DeleteTermAsync extends AsyncTask<Term, Void, Void>
    {
        private TermDao termDao;

        public DeleteTermAsync(TermDao termDao)
        {
            this.termDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms)
        {
            termDao.deleteTerm(terms[0]);
            return null;
        }
    }
}
