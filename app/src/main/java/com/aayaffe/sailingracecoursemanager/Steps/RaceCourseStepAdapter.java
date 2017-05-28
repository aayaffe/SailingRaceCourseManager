package com.aayaffe.sailingracecoursemanager.Steps;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.aayaffe.sailingracecoursemanager.R;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 11/03/2017.
 */

public class RaceCourseStepAdapter extends AbstractFragmentStepAdapter {
    public static final String CURRENT_STEP_POSITION_KEY = "CURRENT_STEP_POSITION_KEY";

    public RaceCourseStepAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return ChooseRaceCourseStep.newInstance(R.layout.step_choose_race_course);
            case 1:
                return ChooseRaceCourseStep.newInstance(R.layout.step_choose_course_options);
//            case 2:
//                return ChooseRaceCourseStep.newInstance(R.layout.step_choose_course_length);
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        return new StepViewModel.Builder(context)
                .setTitle("tab_title") //can be a CharSequence instead
                .create();
    }
}
