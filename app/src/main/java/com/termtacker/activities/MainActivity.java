package com.termtacker.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;

import com.termtacker.data.AppDatabase;
import com.termtacker.R;

public class MainActivity extends AppCompatActivity
{
    //region intentIds
    public static final int ASSESSMENT_REQUEST = 1;
    public static final int COURSE_REQUEST = 2;
    public static final int MENTOR_REQUEST = 3;
    public static final int TERMS_REQUEST = 4;
    //endregion
    //region conversions
    /* DP Conversions:  px = dp * (dpi / 160) */
    float dpi;
    int _8dp;
    int _16dp;
    int _24dp;
    int _48dp;
    //endregion

    LinearLayoutCompat linearLayout;
//    LinearLayout linearLayout;
//    LinearLayoutCompat buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setDpSettings();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int orientation = getResources().getConfiguration().orientation;


        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
//            if (linearLayout != null)
//                linearLayout.removeAllViews();
            createPortraitView();
        }
        else
        {
//            if (linearLayout != null)
//                linearLayout.removeAllViews();
//            if (buttonLayout != null)
//                buttonLayout.removeAllViews();
            createLandscapeView();
        }

    }



    private void createPortraitView()
    {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setLayoutParams(relativeParams);
        relativeLayout.removeAllViews();
        setContentView(relativeLayout);


        linearLayout = new LinearLayoutCompat(MainActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setLayoutParams(params);

        linearLayout.setOrientation(LinearLayoutCompat.VERTICAL);
//        linearLayout.removeAllViews();

        addLogo();
        loadButtons(false);

        relativeLayout.addView(linearLayout);
    }

    private void createLandscapeView()
    {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setLayoutParams(relativeParams);

        setContentView(relativeLayout);


        linearLayout = new LinearLayoutCompat(MainActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        linearLayout.setLayoutParams(params);

        linearLayout.setOrientation(LinearLayoutCompat.VERTICAL);
//        buttonLayout = new LinearLayout(MainActivity.this);
//        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
//        );
//
//        buttonLayout.setLayoutParams(buttonParams);
//        buttonLayout.setOrientation(LinearLayout.VERTICAL);

//        linearLayout.removeAllViews();
        loadButtons(true);

        addLogo();

        relativeLayout.addView(linearLayout);
    }

    /**
     * Sets the DP settings for use in the application
     */
    private void setDpSettings()
    {
        dpi = getResources().getDisplayMetrics().density;
        _8dp = (int) (8 * (dpi / 160));
        _16dp = (int) (16 * (dpi / 160));
        _24dp = (int) (24 * (dpi / 160));
        _48dp = (int) (48 * (dpi / 160));
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
    private void loadButtons(boolean isLandscape)
    {
        int[] buttonLabels = new int[]{
                R.string.btnTerms,
                R.string.btnCourses,
                R.string.btnAssessments
        };

//        buttonLayout.removeAllViews();

        //create a button for each of the main areas and set their onClickListener()
        for (int label : buttonLabels) {
            Button button = new Button(this);
            RelativeLayout.LayoutParams buttonParams =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);

            buttonParams.setMargins(_16dp, _8dp, _16dp, _48dp);
            button.setLayoutParams(buttonParams);
            button.setPadding(_8dp, _48dp, _8dp, _48dp);
            button.setText(label);
            button.setTextSize(32);

            switch (label) {
                case R.string.btnTerms:
                    button.setId(R.id.btnTerms);
                    break;
                case R.string.btnCourses:
                    button.setId(R.id.btnCourses);
                    break;
                case R.string.btnAssessments:
                    button.setId(R.id.btnAssessments);
                    break;
            }

            button.setOnClickListener((v) -> {
                int id = v.getId();
                Intent intent;

                switch (id) {
                    case R.id.btnTerms:
                        loadTermsActivity(v);
                        break;
                    case R.id.btnCourses:
                        loadCoursesActivity(v);
                        break;
                    case R.id.btnAssessments:
                        loadAssessmentActivity(v);
                        break;
                }
            });

            linearLayout.addView(button);
//            if (isLandscape) {
//                buttonLayout.addView(button);
////                linearLayout.addView(buttonLayout);
//            }
//            else {
//
//                linearLayout.addView(button);
//            }
        }
    }

    /**
     * This method is called when the onClick event is fired for the
     * Assessments button.
     *
     * @param view
     */
    private void loadAssessmentActivity(View view)
    {
        Intent intent = new Intent(this, AssessmentsActivity.class);
        startActivityForResult(intent, ASSESSMENT_REQUEST);
    }

    /**
     * This method is called when the onClick event is fired for the
     * Courses button.
     *
     * @param view
     */
    private void loadCoursesActivity(View view)
    {
        Intent intent = new Intent(this, CoursesActivity.class);
        startActivityForResult(intent, COURSE_REQUEST);
    }

    /**
     * This method is called when the onClick event is fired for the
     * Terms button.
     *
     * @param view
     */
    private void loadTermsActivity(View view)
    {
        Intent intent = new Intent(this, TermActivity.class);
        startActivityForResult(intent, TERMS_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.go_to_assessments:
                intent = new Intent(this, AssessmentsActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_courses:
                intent = new Intent(this, CoursesActivity.class);
                startActivity(intent);
                return true;
            case R.id.go_to_terms:
                intent = new Intent(this, TermActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
