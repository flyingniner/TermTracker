package com.termtacker;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity
{
    //region intentIds
    int assessmentIntent = 111;
    int courseIntent = 222;
    int TERMS_REQUEST = 333;
    //endregion
    //region conversions
    /* DP Conversions:  px = dp * (dpi / 160) */
    float dpi;
    int _8dp;
    int _16dp;
    int _24dp;
    int _48dp;
    //endregion

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setDpSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setLayoutParams(relativeParams);
        setContentView(relativeLayout);

        linearLayout = new LinearLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        addLogo();
        loadButtons();

        relativeLayout.addView(linearLayout);

    }

    /**
     * Sets the DP settings for use in the application
     */
    private void setDpSettings()
    {
        dpi = getResources().getDisplayMetrics().density;
        _8dp = (int) (8 * (dpi/ 160));
        _16dp = (int)(16 * (dpi / 160));
        _24dp = (int)(24 * (dpi / 160));
        _48dp = (int)(48 * (dpi / 160));
    }

    /**
     * Adds the app image to the layout.
     */
    private void addLogo()
    {
        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams imageParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

        imageView.setImageDrawable(
                getResources().getDrawable(
                        R.drawable.owl,
                        getApplicationContext().getTheme()));

        linearLayout.addView(imageView);
    }

    /**
     * Creates the buttons for the Main activity and
     * adds them to the linearLayout.
     */
    private void loadButtons()
    {
        int[] buttonLabels = new int[] {
                R.string.btnAssessments,
                R.string.btnCourses,
                R.string.btnTerms
        };

        //create a button for each of the main areas and set their onClickListener()
        for (int label : buttonLabels)
        {
            Button button = new Button(this);
            RelativeLayout.LayoutParams buttonParams =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);

            buttonParams.setMargins(_16dp, _8dp, _16dp, _48dp);
            button.setLayoutParams(buttonParams);
            button.setPadding(_8dp,_48dp,_8dp,_48dp);
            button.setText(label);
            button.setTextSize(32);

            switch (label)
            {
                case R.string.btnAssessments:
                    button.setId(R.id.btnAssessments);
                    break;
                case R.string.btnCourses:
                    button.setId(R.id.btnCourses);
                    break;
                case R.string.btnTerms:
                    button.setId(R.id.btnTerms);
                    break;
            }

            button.setOnClickListener((v) -> {
                int id = v.getId();
                Intent intent;

                switch (id) {
                    case R.id.btnAssessments:
                        loadAssessmentActivity(v);
                        break;
                    case R.id.btnCourses:
                        loadCoursesActivity(v);
                        break;
                    case R.id.btnTerms:
                        loadTermsActivity(v);
                        break;
                }
            });

            linearLayout.addView(button);
        }
    }

    /**
     * This method is called when the onClick event is fired for the
     * Assessments button.
     * @param view
     */
    private void loadAssessmentActivity(View view)
    {
//        Intent intent = new Intent(this, AssessmentActivity.class);
//        startActivityForResult(intent,assessmentIntent);
    }

    /**
     * This method is called when the onClick event is fired for the
     * Courses button.
     * @param view
     */
    private void loadCoursesActivity(View view)
    {
//        Intent intent = new Intent(this, CoursesActivity.class);
//        startActivityForResult(intent,courseIntent);
    }

    /**
     * This method is called when the onClick event is fired for the
     * Terms button.
     * @param view
     */
    private void loadTermsActivity(View view)
    {
        Intent intent = new Intent(this, TermActivity.class);
        startActivityForResult(intent, TERMS_REQUEST);
    }
}
