package com.termtacker.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termtacker.models.CourseAdapter;
import com.termtacker.R;
import com.termtacker.models.Status;
import com.termtacker.models.Course;
import com.termtacker.utilities.CourseViewModel;

import java.time.LocalDate;

public class CoursesActivity extends AppCompatActivity
{
    public static final String TAG = CoursesActivity.class.getCanonicalName();
    private CourseViewModel courseViewModel;
    public static final int ADD_COURSE_REQUEST = 5;
    public static final int EDIT_COURSE_REQUEST = 6;



    public static final String EXTRA_ID = "com.termtracker.CoursesActivity.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.termtracker.CoursesActivity.EXTRA_TITLE";
    public static final String EXTRA_START = "com.termtracker.CoursesActivity.EXTRA_START";
    public static final String EXTRA_END = "com.termtracker.CoursesActivity.EXTRA_END";
    public static final String EXTRA_STATUS = "com.termtracker.CoursesActivity.EXTRA_STATUS";
    public static final String EXTRA_TERMID = "com.termtracker.CoursesActivity.EXTRA_TERMID";
    public static final String EXTRA_MENTORID = "com.termtracker.CoursesActivity.EXTRA_MENTORID";
    public static final String EXTRA_TERM_END = "com.termtracker.CoursesActivity.EXTRA_TERM_END";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        setTitle("Courses");

        FloatingActionButton buttonAddCourse = findViewById(R.id.courses_add_course);
        buttonAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseAddEditActivity.class);
            startActivityForResult(intent, ADD_COURSE_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.courses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CourseAdapter courseAdapter = new CourseAdapter();
        recyclerView.setAdapter(courseAdapter);

        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        courseViewModel.getAllCourses().observe(this, list -> courseAdapter.submitList(list)
        );



        courseAdapter.setOnItemClickListener(course -> {
            Intent intent = new Intent(this, CourseAddEditActivity.class);
            intent.putExtra(EXTRA_ID, course.getCourseId());
            intent.putExtra(EXTRA_TITLE, course.getTitle());
            intent.putExtra(EXTRA_START, course.getStartDate().toEpochDay());
            intent.putExtra(EXTRA_END, course.getEndDate().toEpochDay());
            intent.putExtra(EXTRA_STATUS, course.getStatus());
            intent.putExtra(EXTRA_TERMID, course.getTermId());
            intent.putExtra(EXTRA_MENTORID, course.getCourseMentorId());
            intent.putExtra(EXTRA_TERM_END, courseViewModel.getTermEndDate(course.getTermId()).toEpochDay());

            startActivityForResult(intent, EDIT_COURSE_REQUEST);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView
                    .ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {

                Course swipedCourse = courseAdapter.getCourseAt(viewHolder.getAdapterPosition());

                if (swipedCourse.getStatus() == Status.PENDING &&
                        swipedCourse.getTermId() == 0) {
                    courseViewModel.deleteCourse(swipedCourse);
                    Toast.makeText(CoursesActivity.this,
                                   "Course Deleted!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CoursesActivity.this,
                                   "Course can't be deleted because it's associated to a Term",
                                   Toast.LENGTH_LONG).show();
                }

            }
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
            }
            else if (requestCode == EDIT_COURSE_REQUEST) {
                int courseId = data.getIntExtra(EXTRA_ID, -1);
                course = new Course(courseId, title, start, end, status, mentorId, termId);
                courseViewModel.updateCourse(course);
                Toast.makeText(CoursesActivity.this,
                               "Coursed Updated!", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(CoursesActivity.this,
                           "Course was not able to be saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.course_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        //TODO: figure out how to move between screens from the menu?
        switch (item.getItemId())
        {
            case R.id.go_to_assessments:
//                intent = new Intent(this, CoursesActivity.class); //TODO: change to AssessmentActivity.class
//                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_mentors:
//                intent = new Intent(this, CoursesActivity.class); //TODO: change to MentorActivity.class
//                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent,0);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }
}
