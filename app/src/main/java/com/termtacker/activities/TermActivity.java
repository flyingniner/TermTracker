package com.termtacker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termtacker.R;
import com.termtacker.data.TermRepository;
import com.termtacker.models.Course;
import com.termtacker.models.Status;
import com.termtacker.models.Term;
import com.termtacker.models.TermAdapter;
import com.termtacker.utilities.CourseViewModel;
import com.termtacker.utilities.CoursesViewModelFactory;
import com.termtacker.utilities.TermViewModel;

import java.time.LocalDate;
import java.util.List;

public class TermActivity extends AppCompatActivity
{
    public static final String TAG = TermActivity.class.getCanonicalName();
    private TermViewModel termViewModel;
    public static final int ADD_TERM_REQUEST = 1;
    public static final int EDIT_TERM_REQUEST = 2;


    public static final String EXTRA_ID = "com.termtracker.TermActivity.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.termtracker.TermActivity.EXTRA_TITLE";
    public static final String EXTRA_START = "com.termtracker.TermActivity.EXTRA_START";
    public static final String EXTRA_END = "com.termtracker.TermActivity.EXTRA_END";
    public static final String EXTRA_STATUS = "com.termtracker.TermActivity.EXTRA_STATUS";

    private RecyclerView recyclerView;
    private TermAdapter termAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        setTitle("Terms");

        setupButtonListeners();

        setupRecyclerView();

        termViewModel = ViewModelProviders.of(this).get(TermViewModel.class);
        termViewModel.getAllterms().observe(this, terms -> {
            Log.d("TermsCount", String.valueOf(terms.size()));
            termAdapter.submitList(terms);
        });

        setupAdapterListeners();
    }

    private void setupAdapterListeners()
    {
        termAdapter.setOnItemClickListener(new TermAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(Term term)
            {
                Intent intent = new Intent(TermActivity.this, TermAddEditActivity.class);
                intent.putExtra(EXTRA_ID, term.getTermId());
                intent.putExtra(EXTRA_TITLE, term.getTitle());
                intent.putExtra(EXTRA_START, term.getStartDate().toEpochDay());
                intent.putExtra(EXTRA_END, term.getEndDate().toEpochDay());
                intent.putExtra(EXTRA_STATUS, term.getStatus());
                startActivityForResult(intent, EDIT_TERM_REQUEST);
            }
        });

        termAdapter.setOnLongItemClickListener(term -> {
            TermRepository termRepository = new TermRepository(getApplication());
            int courseCount = termRepository.getCourseCountForTerm(term.getTermId());

            if (courseCount > 0) //display error b/c course has assessments and can't be deleted
            {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.error_title))
                        .setMessage(getString(R.string.term_has_courses_error))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.delete_term_title))
                        .setMessage(getString(R.string.term_confirm_delete_msg))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            termViewModel.deleteTerm(term);
                            Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_LONG).show();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }

        });
    }

    private void setupButtonListeners()
    {
        FloatingActionButton buttonAddTerm = findViewById(R.id.term_activity_floating_add_term);
        buttonAddTerm.setOnClickListener(v -> {
            Intent intent = new Intent(TermActivity.this, TermAddEditActivity.class);
            startActivityForResult(intent, ADD_TERM_REQUEST);
        });
    }

    private void setupRecyclerView()
    {
        recyclerView = findViewById(R.id.term_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        termAdapter = new TermAdapter();
        recyclerView.setAdapter(termAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.term_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId())
        {
            case R.id.go_to_assessments:
                intent = new Intent(this, AssessmentsActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_courses:
                intent = new Intent(this, CoursesActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent,0);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
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
            title = data.getStringExtra(EXTRA_TITLE);
            start = LocalDate.ofEpochDay(data.getLongExtra(EXTRA_START, 0));
            end = LocalDate.ofEpochDay(data.getLongExtra(EXTRA_END, 0));
            int termId = data.getIntExtra(EXTRA_ID, 0);
            status = determineStatusForTerm(start, end, termId);

            if (requestCode == EDIT_TERM_REQUEST) {
                id = data.getIntExtra(EXTRA_ID, -1);
                Term term = new Term(id, title, start, end, status);
                termViewModel.updateTerm(term);
                Toast.makeText(this, "Term updated", Toast.LENGTH_SHORT);
            } else {
                Term term = new Term(title, start, end, status);
                termViewModel.insertTerm(term);
                Toast.makeText(this, "Term added", Toast.LENGTH_SHORT);
            }
        }

    }

    private String determineStatusForTerm(LocalDate start, LocalDate end, int termId)
    {
        CoursesViewModelFactory factory = new CoursesViewModelFactory(getApplication(), termId, false);
        CourseViewModel courseViewModel = ViewModelProviders.of(TermActivity.this, factory).get(CourseViewModel.class);
        List<Course> courseList = courseViewModel.getStaticFilteredCourses(termId);
        LocalDate now = LocalDate.now();

        if (start.isAfter(now)) //term hasn't started
        {
            return Status.PENDING;
        }

        if (end.isAfter(now)) //term has started and is in progress
        {
            return Status.IN_PROGRESS;
        }

        for (Course c : courseList)
        {
            String status = c.getStatus();

            if (status.equals(Status.INCOMPLETE))
            {
                return Status.INCOMPLETE;
            }
        }

        return Status.COMPLETED;
//
//
//
//
//        String status = Status.PASSED;
//        for (Course c : courseList)
//        {
//
//
//        }
//
//        LocalDate now = LocalDate.now();
//        if (start.isAfter(now)) {
//            status = Status.PENDING;
//        } else if ((start.isEqual(now) || start.isBefore(now)) && end.isAfter(now)) {
//            status = Status.IN_PROGRESS;
//
//        } else {
//            status = Status.COMPLETED;
//        }
//        return status;
    }
}
