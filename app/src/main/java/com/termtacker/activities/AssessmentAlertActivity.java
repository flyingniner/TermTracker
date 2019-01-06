package com.termtacker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.termtacker.R;
import com.termtacker.utilities.TermReceiver;
import com.termtacker.utilities.Utils;

import java.time.LocalDate;

public class AssessmentAlertActivity extends AppCompatActivity
{
    private int assessmentId;
    private String assessmentName;
    private String assessmentType;
    private LocalDate assessmentDate;

    private TextView textViewCourseName;
    private TextView textViewAssessmentType;
    private TextView textViewScheduled;
    private CheckBox checkBoxReminder;
    private Button buttonSave;

    public static final String EXTRA_ASSESSMENT_ID = "com.termtracker.AssessmentAlertActivity_ID";
    public static final String EXTRA_ASSESSMENT_TITLE = "com.termtracker.AssessmentAlertActivity_NAME";
    public static final String EXTRA_ASSESSMENT_DESC = "com.termtracker.AssessmentAlertActivity_DESC";
    public static final String EXTRA_ASSESSMENT_DATE = "com.termtracker.AssessmentAlertActivity_DATE";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_alert);

        getUIFieldReferences();
        loadUIFiledValues();

        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkBoxReminder.isChecked())
                {
                    Intent alertData = new Intent(AssessmentAlertActivity.this, TermReceiver.class);

                    alertData.putExtra(EXTRA_ASSESSMENT_ID, assessmentId);
                    alertData.putExtra(EXTRA_ASSESSMENT_TITLE, assessmentName + " Assessment today");
                    alertData.putExtra(EXTRA_ASSESSMENT_DESC, "You are scheduled to take a(n) " + assessmentType + " today!");
                    alertData.putExtra(EXTRA_ASSESSMENT_DATE, assessmentDate.toEpochDay());

                    PendingIntent sender = PendingIntent.getBroadcast(AssessmentAlertActivity.this,
                            3, alertData, 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                            Utils.convertStringDateToMillis(assessmentDate.format(Utils.dateFormatter_MMddyyyy)),
                            sender);
                    setResult(RESULT_OK, alertData);

                }

                finish();
            }
        });


    }

    private void getUIFieldReferences()
    {
        textViewCourseName = findViewById(R.id.assessment_alert_course_name);
        textViewAssessmentType = findViewById(R.id.assessment_alert_assessmenttype);
        textViewScheduled = findViewById(R.id.assessment_alert_scheduled);
        checkBoxReminder = findViewById(R.id.assessment_alert_reminder);
        buttonSave = findViewById(R.id.assessment_alert_button_save);
    }

    private void loadUIFiledValues()
    {
        Intent intent = getIntent();
        assessmentId = intent.getIntExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_ID, 0);
        assessmentName = intent.getStringExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_TITLE);
        assessmentType = intent.getStringExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_TYPE);
        assessmentDate = LocalDate.ofEpochDay(intent.getLongExtra(AssessmentAddEditActivity.EXTRA_ASSESSMENT_DATE, 0));

        textViewCourseName.setText(assessmentName);
        textViewAssessmentType.setText(assessmentType);
        textViewScheduled.setText(assessmentDate.format(Utils.dateFormatter_MMddyyyy));
    }
}
