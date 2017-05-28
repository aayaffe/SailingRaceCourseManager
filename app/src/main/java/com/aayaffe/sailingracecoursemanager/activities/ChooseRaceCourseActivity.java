package com.aayaffe.sailingracecoursemanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Steps.RaceCourseStepAdapter;
import com.stepstone.stepper.StepperLayout;

/**
 * Created by aayaffe on 11/03/2017.
 */

public class ChooseRaceCourseActivity extends AppCompatActivity {
    private StepperLayout mStepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new RaceCourseStepAdapter(getSupportFragmentManager(), this));

    }
}
