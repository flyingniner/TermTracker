package com.termtacker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

public class TermActivity extends AppCompatActivity
{
    public static final String TAG = TermActivity.class.getCanonicalName();
    private TermViewModel termViewModel;
    public static final int ADD_TERM_REQUEST = 1;
    public static final int EDIT_TERM_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        FloatingActionButton buttonAddTerm = findViewById(R.id.term_activity_floating_add_term);
        buttonAddTerm.setOnClickListener(v -> {
            Intent intent = new Intent(TermActivity.this, TermAddEditActivity.class);
            startActivityForResult(intent, ADD_TERM_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.term_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TermAdapter termAdapter = new TermAdapter();
        recyclerView.setAdapter(termAdapter);

        //TODO: uncomment below after TermViewModel class is created
        termViewModel = ViewModelProviders.of(this).get(TermViewModel.class);
        termViewModel.getAllterms().observe(this, terms -> {
            Log.d("TermsCount", String.valueOf(terms.size()));
            termAdapter.submitList(terms);
        });

        termAdapter.setOnItemClickListener(new TermAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(Term term)
            {
                Intent intent = new Intent(TermActivity.this, TermAddEditActivity.class);
                Log.d(TAG, "ID: " + term.getTermId());
                Log.d(TAG, "TITLE: " + term.getTitle());
                Log.d(TAG, "START: " + term.getStartDate().format(Utils.dateFormatter_MMddyyyy));
                Log.d(TAG, "END: " + term.getEndDate().format(Utils.dateFormatter_MMddyyyy));
                Log.d(TAG, "PROGRESS: " + term.getStatus());
                intent.putExtra(TermAddEditActivity.EXTRA_ID, term.getTermId());
                intent.putExtra(TermAddEditActivity.EXTRA_TITLE, term.getTitle());
                intent.putExtra(TermAddEditActivity.EXTRA_START, term.getStartDate().format(Utils.dateFormatter_MMddyyyy));
                intent.putExtra(TermAddEditActivity.EXTRA_END, term.getEndDate().format(Utils.dateFormatter_MMddyyyy));
                intent.putExtra(TermAddEditActivity.EXTRA_STATUS, term.getStatus());
                startActivityForResult(intent, EDIT_TERM_REQUEST);
            }
        });

        //TODO: add itemTouchHelper (see MainActivity.OnCreate from example project
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.term_menu, menu);
        return true;
    }

    /**
     * Handles the result from TermAddEditActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        int id;
        String title, status;
        LocalDate start, end;

        if (resultCode == RESULT_OK) {
            title = data.getStringExtra(TermAddEditActivity.EXTRA_TITLE);
            start = LocalDate.ofEpochDay(data.getLongExtra(TermAddEditActivity.EXTRA_START, 0));
            end = LocalDate.ofEpochDay(data.getLongExtra(TermAddEditActivity.EXTRA_END, 0));

            Log.d(TAG, "return Start: " + start.format(Utils.dateFormatter_MMddyyyy));
            Log.d(TAG, "return End: " + end.format(Utils.dateFormatter_MMddyyyy));

            status = determineStatusForTerm(start, end);

            if (requestCode == EDIT_TERM_REQUEST) {
                id = data.getIntExtra(TermAddEditActivity.EXTRA_ID, -1);
                Term term = new Term(id, title, start, end, status);
                termViewModel.update(term);
                Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT);
            } else {
                Term term = new Term(title, start, end, status);
                termViewModel.insert(term);
                Toast.makeText(this, "Term added", Toast.LENGTH_SHORT);
            }
        }

    }

    private String determineStatusForTerm(LocalDate start, LocalDate end)
    {
        String status;
        LocalDate now = LocalDate.now();
        if (start.isAfter(now)) {
            status = Status.PENDING;
        } else if ((start.isEqual(now) || start.isBefore(now)) && end.isAfter(now)) {
            status = Status.IN_PROGRESS;

        } else {
            status = Status.COMPLETED;
        }
        return status;
    }
}
