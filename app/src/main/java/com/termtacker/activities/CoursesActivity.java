package com.termtacker.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termtacker.data.AssessmentRepository;
import com.termtacker.models.CourseAdapter;
import com.termtacker.R;
import com.termtacker.models.Course;
import com.termtacker.utilities.CourseViewModel;
import com.termtacker.utilities.CoursesViewModelFactory;

import java.time.DateTimeException;
import java.time.LocalDate;

public class CoursesActivity extends AppCompatActivity
{
    public static final String TAG = CoursesActivity.class.getCanonicalName();
    private CourseViewModel courseViewModel;
    public static final int ADD_COURSE_REQUEST = 5;
    public static final int EDIT_COURSE_REQUEST = 6;


    public static final String EXTRA_IS_NEW_COURSE = "com.termtracker.CourseActivity.EXTRA_IS_NEW_COURSE";
    public static final String EXTRA_ID = "com.termtracker.CoursesActivity.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.termtracker.CoursesActivity.EXTRA_TITLE";
    public static final String EXTRA_START = "com.termtracker.CoursesActivity.EXTRA_START";
    public static final String EXTRA_END = "com.termtracker.CoursesActivity.EXTRA_END";
    public static final String EXTRA_STATUS = "com.termtracker.CoursesActivity.EXTRA_STATUS";
    public static final String EXTRA_TERMID = "com.termtracker.CoursesActivity.EXTRA_TERMID";
    public static final String EXTRA_MENTORID = "com.termtracker.CoursesActivity.EXTRA_MENTORID";
    public static final String EXTRA_TERM_START = "com.termtracker.CoursesActivity.EXTRA_TERM_START";
    public static final String EXTRA_TERM_END = "com.termtracker.CoursesActivity.EXTRA_TERM_END";
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private boolean isTermAddEditActivityCaller = false;
    private Intent intentFromCaller;
    private int termId = 0;
    boolean showOnlyAvailableCourses = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        intentFromCaller = getIntent();
        if (intentFromCaller.hasExtra(TermAddEditActivity.EXTRA_TERM_ID))
        {
            isTermAddEditActivityCaller = true;
            setTitle("Select a Course");
            termId = intentFromCaller.getIntExtra(TermAddEditActivity.EXTRA_TERM_ID, 0);
            showOnlyAvailableCourses = true;
        }
        else
        {
            setTitle("Courses");
        }

        setupButtonListeners();

        setupRecyclerView();

        CoursesViewModelFactory factory = new CoursesViewModelFactory(getApplication(), termId, showOnlyAvailableCourses);
        courseViewModel = ViewModelProviders.of(this, factory).get(CourseViewModel.class);
        courseViewModel.getAllCourses().observe(this, list -> courseAdapter.submitList(list)
        );


        setupAdapterListeners();

    }

    private void setupAdapterListeners()
    {
        courseAdapter.setOnItemClickListener(course -> {

            if (isTermAddEditActivityCaller) {

                course.setTermId(intentFromCaller.getIntExtra(TermAddEditActivity.EXTRA_TERM_ID, 0));
                courseViewModel.updateCourse(course);

                Intent data = new Intent();
                data.putExtra(EXTRA_ID, course.getCourseId());
                setResult(RESULT_OK, data);
                finish();
            } else {

                Intent intent = new Intent(this, CourseAddEditActivity.class);
                intent.putExtra(EXTRA_ID, course.getCourseId());
                intent.putExtra(EXTRA_TITLE, course.getTitle());
                intent.putExtra(EXTRA_START, course.getStartDate().toEpochDay());
                intent.putExtra(EXTRA_END, course.getEndDate().toEpochDay());
                intent.putExtra(EXTRA_STATUS, course.getStatus());
                intent.putExtra(EXTRA_TERMID, course.getTermId());
                intent.putExtra(EXTRA_MENTORID, course.getCourseMentorId());

                long start = 0;
                long end = 0;
                try {
                    start = courseViewModel.getTermStartDate(course.getCourseId()).toEpochDay();
                }
                catch (NullPointerException npe) {
                    start = 0;
                }
                catch (DateTimeException dte) {
                    start = 0;
                }try {
                    end = courseViewModel.getTermEndDate(course.getCourseId()).toEpochDay();
                }
                catch (NullPointerException npe) {
                    end = 0;
                }
                catch (DateTimeException dte) {
                    end = 0;
                }

                intent.putExtra(EXTRA_TERM_START, start);
                intent.putExtra(EXTRA_TERM_END, end);



                startActivityForResult(intent, EDIT_COURSE_REQUEST);
            }
        });

        courseAdapter.setOnLongItemClickListener(course -> {
            AssessmentRepository assessmentRepository = new AssessmentRepository(getApplication());
            int assessmentCount = assessmentRepository.getAssessmentCountForCourse(course.getCourseId());

            if (assessmentCount > 0) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.error_title))
                        .setMessage(getString(R.string.course_has_assessments_error))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setNeutralButton(R.string.ok, null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.delete_course_title))
                        .setMessage(getString(R.string.course_confirm_delete_msg))
                        .setIcon(R.drawable.ic_incomplete_72dp)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            courseViewModel.deleteCourse(course);
                            Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_LONG).show();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }

        });
    }

    private void setupRecyclerView()
    {
        recyclerView = findViewById(R.id.courses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        courseAdapter = new CourseAdapter();
        recyclerView.setAdapter(courseAdapter);
    }

    /**
     * Listens for the on-click
     */
    private void setupButtonListeners()
    {
        FloatingActionButton buttonAddCourse = findViewById(R.id.courses_add_course);
        buttonAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseAddEditActivity.class);
            intent.putExtra(EXTRA_IS_NEW_COURSE, true);
            startActivityForResult(intent, ADD_COURSE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            Course course;

            String title = data.getStringExtra(EXTRA_TITLE);
            LocalDate start = LocalDate.ofEpochDay(data.getLongExtra(EXTRA_START, -1));
            LocalDate end = LocalDate.ofEpochDay(data.getLongExtra(EXTRA_END, -1));
            String status = data.getStringExtra(EXTRA_STATUS);
            int termId = data.getIntExtra(EXTRA_TERMID, -1);
            int mentorId = data.getIntExtra(EXTRA_MENTORID, -1);

            if (requestCode == ADD_COURSE_REQUEST) {
                course = new Course(title, start, end, status, mentorId, termId);
                courseViewModel.insertCourse(course);
                Toast.makeText(CoursesActivity.this,
                        "Coursed Added!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_COURSE_REQUEST) {
                int courseId = data.getIntExtra(EXTRA_ID, -1);
                course = new Course(courseId, title, start, end, status, mentorId, termId);
                courseViewModel.updateCourse(course);
                Toast.makeText(CoursesActivity.this,
                        "Coursed Updated!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(CoursesActivity.this,
                    "Course was not able to be saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        if (isTermAddEditActivityCaller)
            menuInflater.inflate(R.menu.select_a_course, menu);
        else
            menuInflater.inflate(R.menu.course_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        //TODO: figure out how to move between screens from the menu?
        switch (item.getItemId()) {
            case R.id.go_to_assessments:
                intentFromCaller = new Intent(this, AssessmentsActivity.class); //TODO: change to AssessmentActivity.class
                startActivity(intentFromCaller);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
