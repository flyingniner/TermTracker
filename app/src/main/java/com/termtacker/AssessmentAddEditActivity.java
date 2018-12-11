package com.termtacker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class AssessmentAddEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    private String assessmentType;
    private Spinner spinner;
    private DatePicker datePicker;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private EditText editTextScheduled;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_add_edit);

        spinner = findViewById(R.id.assessment_type_spinner);
        spinner.setOnItemClickListener(this::onItemSelected);

        List<String> assessmentTypes = new ArrayList<>();
        assessmentTypes.add(getResources().getString(R.string.assessmentTypePre));
        assessmentTypes.add(getResources().getString(R.string.assessmentTypeObj));
        assessmentTypes.add(getResources().getString(R.string.assessmentTypePrj));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                R.layout.activity_assessment_add_edit,
                assessmentTypes);

        dataAdapter.setDropDownViewResource(R.layout.activity_assessment_add_edit);
        spinner.setAdapter(dataAdapter);


        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(
                        AssessmentAddEditActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)

                    {
                        editTextScheduled.setText(
                                (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        Intent intent = getIntent();
//        if (intent.hasExtra(AssessmentsActivity.EXTRA_ID))
//        {
//
//        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        assessmentType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
