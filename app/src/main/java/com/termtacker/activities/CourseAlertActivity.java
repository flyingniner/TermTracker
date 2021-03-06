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
import java.util.ArrayList;
import java.util.List;

public class CourseAlertActivity extends AppCompatActivity
{
    private int courseId;
    private TextView courseName;
    private TextView courseStart;
    private TextView courseEnd;
    private CheckBox startReminder;
    private CheckBox endReminder;
    private Button saveButton;

    public static final String EXTRA_TITLE = "com.termtracker.CourseAlertActivity_EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.termtracker.CourseAlertActivity_EXTRA_DESCRIPTION";
    public static final String EXTRA_COURSE_ID = "com.termtracker.CourseAlertActivity_EXTRA_COURSE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_alert);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getUIReferences();
        loadUIElementValues();
        setupSaveButton();
    }


    /**
     * Sets up the Save button listener and creates a notification that will fire on the
     * reminder date
     */
    private void setupSaveButton()
    {
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent data = new Intent(CourseAlertActivity.this, TermReceiver.class);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                List<String> alerts = createAlertTitle();
                for (String s : alerts)
                {
                    data.putExtra(CourseAlertActivity.EXTRA_TITLE, s);
                    data.putExtra(CourseAlertActivity.EXTRA_DESCRIPTION, createAlertDesc(s));
                    data.putExtra(CourseAlertActivity.EXTRA_COURSE_ID, courseId);

                    PendingIntent sender;

                    if (s.contains("starts"))
                    {
                        sender = PendingIntent.getBroadcast(
                                CourseAlertActivity.this, 0, data, 0);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                Utils.convertStringDateToMillis(courseStart.getText().toString()),
                                sender);
                    }
                    else
                    {
                        sender = PendingIntent.getBroadcast(
                                CourseAlertActivity.this, 1, data, 0);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                Utils.convertStringDateToMillis(courseEnd.getText().toString()),
                                sender);
                    }
                }

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }


    /**
     * Returns the appropriate message for the notification
     *
     * @param s
     * @return
     */
    private String createAlertDesc(String s)
    {
        if (s.contains("starts"))
            return "You have a new course starting today.";
        else
        return "Your class is scheduled to end today.";
    }


    /**
     * Creates the appropriate title for the notification
     * @return
     */
    private List<String> createAlertTitle()
    {
        List<String> alertTitles = new ArrayList<>();
        if (startReminder.isChecked()) {
            alertTitles.add(new String(courseName.getText().toString() + " starts today!"));
        }
        if (endReminder.isChecked()) {
            alertTitles.add(new String(courseName.getText().toString() + " ends today!"));
        }
        return alertTitles;
    }


    /**
     * Loads UI elements with values
     */
    private void loadUIElementValues()
    {
        Intent intent = getIntent();

        courseId = intent.getIntExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_ID, 0);
        courseName.setText(intent.getStringExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_NAME));
        courseStart.setText(LocalDate.ofEpochDay(
                intent.getLongExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_START, 0))
                .format(Utils.dateFormatter_MMddyyyy));

        courseEnd.setText(LocalDate.ofEpochDay(
                intent.getLongExtra(CourseAddEditActivity.EXTRA_ALERT_COURSE_END, 0))
                .format(Utils.dateFormatter_MMddyyyy));
    }


    /**
     * Binds the UI elements to their respective IDs
     */
    private void getUIReferences()
    {
        courseName = findViewById(R.id.course_alert_course_name);
        courseStart = findViewById(R.id.course_alert_start);
        courseEnd = findViewById(R.id.course_alert_end);
        startReminder = findViewById(R.id.course_alert_start_reminder);
        endReminder = findViewById(R.id.course_alert_end_reminder);
        saveButton = findViewById(R.id.course_alert_button_save);
    }
}
