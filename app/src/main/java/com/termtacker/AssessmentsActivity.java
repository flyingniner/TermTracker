package com.termtacker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;

import static com.termtacker.TermActivity.ADD_TERM_REQUEST;

public class AssessmentsActivity extends AppCompatActivity
{
    public static final String EXTRA_ID = "com.termtracker.AssessmentActivity.EXTRA_ID";
    public static final String EXTRA_ASSESSMENT_CODE = "com.termtracker.AssessmentActivity.EXTRA_CODE";
    public static final String EXTRA_ASSESSMENT_SCHEDULED = "com.termtracker.AssessmentActivity.EXTRA_SCHEDULED";
    public static final String EXTRA_ASSESSMENT_TYPE = "com.termtracker.AssessmentActivity.EXTRA_TYPE";
    public static final String EXTRA_ASSESSMENT_RESULT = "com.termtracker.AssessmentActivity.EXTRA_RESULT";
    public static final String EXTRA_ASSESSMENT_COURSE_NAME = "com.termtracker.AssessmentActivity.EXTRA_NAME";
    private FloatingActionButton buttonAddAssessment;
    private AssessmentViewModel assessmentViewModel;
    private CourseViewModel courseViewModel;

    private int ADD_ASSESSMENT_REQUEST = 15;
    private static final int EDIT_ASSESSMENT_REQUEST = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        setTitle("Assessments");

        buttonAddAssessment = findViewById(R.id.assessments_activity_floating_add_assessment);
        buttonAddAssessment.setOnClickListener(v -> {
            Intent intent = new Intent(AssessmentsActivity.this, AssessmentAddEditActivity.class);
            startActivityForResult(intent, ADD_ASSESSMENT_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.assessments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter();
        recyclerView.setAdapter(assessmentAdapter);

        courseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        assessmentViewModel = ViewModelProviders.of(this).get(AssessmentViewModel.class);

        assessmentViewModel.getAllAssessments().observe(this, assessments -> {
            assessmentAdapter.submitList(assessments);
        });

        assessmentAdapter.setOnItemClickListener(new AssessmentAdapter.onItemClickListener()
        {

            @Override
            public void onItemClick(Assessment assessment)
            {

                Intent intent = new Intent(AssessmentsActivity.this, AssessmentAddEditActivity.class);

                intent.putExtra(EXTRA_ASSESSMENT_CODE, assessment.getAssessmentCode());
                intent.putExtra(EXTRA_ASSESSMENT_TYPE, assessment.getAssessmentType());
                intent.putExtra(EXTRA_ASSESSMENT_SCHEDULED, assessment.getAssessmentDate().format(Utils.dateFormatter_MMddyyyy).toString());
                //TODO: need a way to get the name of the course
                int courseId = assessment.getCourseId();
                intent.putExtra(EXTRA_ASSESSMENT_COURSE_NAME, courseViewModel.getCourseName(courseId));
                intent.putExtra(EXTRA_ASSESSMENT_RESULT, assessment.getResult());

                intent.putExtra(EXTRA_ID, assessment.getAssessmentId());
                startActivityForResult(intent, EDIT_ASSESSMENT_REQUEST);
            }
        });

    }

    /**
     * Inflates the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.assessment_menu, menu);
        return true;
    }

    /**
     * Handler for the menu options onSelected event
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId())
        {
            case R.id.go_to_courses:
                intent = new Intent(this, CoursesActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_home:
                intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivityForResult(intent, 0);

            default: return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Assessment assessment = new Assessment();
        if (resultCode == RESULT_OK)
        {
            assessment.setAssessmentCode(data.getStringExtra(EXTRA_ASSESSMENT_CODE));
            assessment.setAssessmentType(data.getStringExtra(EXTRA_ASSESSMENT_TYPE));
            assessment.setResult(data.getStringExtra(EXTRA_ASSESSMENT_RESULT));

            LocalDate date = LocalDate.ofEpochDay(data.getLongExtra(EXTRA_ASSESSMENT_SCHEDULED, 0));
            assessment.setAssessmentDate(date);

            if (requestCode == EDIT_ASSESSMENT_REQUEST)
            {
                assessment.setAssessmentId(data.getIntExtra(EXTRA_ID,0));
                assessment.setCourseId(courseViewModel.getCourseId(data.getStringExtra(EXTRA_ASSESSMENT_COURSE_NAME)));
                assessmentViewModel.updateAssessment(assessment);
                Toast.makeText(this,"Assessment updated",Toast.LENGTH_LONG).show();
            }
            else
            {
                String courseName = data.getStringExtra(EXTRA_ASSESSMENT_COURSE_NAME);
                assessment.setCourseId(courseViewModel.getCourseId(courseName));

                assessmentViewModel.insertAssessment(assessment);
                Toast.makeText(this,"Assessment added",Toast.LENGTH_LONG).show();
            }

        }
    }
}
