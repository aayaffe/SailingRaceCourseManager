package com.aayaffe.sailingracecoursemanager.Steps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.dialogs.CourseTypeRV;
import com.aayaffe.sailingracecoursemanager.dialogs.CourseTypeSecondDialog;
import com.aayaffe.sailingracecoursemanager.initializinglayer.InitialCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by aayaffe on 11/03/2017.
 */

public class ChooseRaceCourseStep extends ButterKnifeFragment implements Step, CourseTypeRV.OnRecyclerItemClickListener {
    private static final String CLICKS_KEY = "clicks";

    private static final int TAP_THRESHOLD = 2;

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";

    private int i = 0;

    private RaceCourseDescriptor2 raceCourseDescriptor;


    public static ChooseRaceCourseStep newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        ChooseRaceCourseStep fragment = new ChooseRaceCourseStep();
        fragment.setArguments(args);
        List<RaceCourseDescriptor2> coursesList = new InitialCourseDescriptor().getRaceCourseDescriptors();

        return fragment;
    }

     @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            //i = savedInstanceState.getInt(CLICKS_KEY);
        }
         List<RaceCourseDescriptor2> coursesList = new InitialCourseDescriptor().getRaceCourseDescriptors();
         RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.course_type_rv_step);
         recyclerView.setHasFixedSize(true);
         RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
         recyclerView.setLayoutManager(layoutManager);
         CourseTypeRV adapter = new CourseTypeRV(coursesList, this);
         recyclerView.setAdapter(adapter);

    }

    @Override
    protected int getLayoutResId() {
        return getArguments().getInt(LAYOUT_RESOURCE_ID_ARG_KEY);
    }

    @Override
    public VerificationError verifyStep() {
        return isAboveThreshold() ? null : new VerificationError("Click " + (TAP_THRESHOLD - i) + " more times!");
    }

    private boolean isAboveThreshold() {
        return i >= TAP_THRESHOLD;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CLICKS_KEY, i);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onSelected() {
        //update UI when selected
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }

    @Override
    public void onRecyclerItemClick(RaceCourseDescriptor2 raceCourseDescriptor){
        this.raceCourseDescriptor = raceCourseDescriptor;

//        CourseTypeSecondDialog dialog = new CourseTypeSecondDialog(getContext(), raceCourseDescriptor);
//
//        dialog.show();
//        dialog.setDialogResult(new CourseTypeSecondDialog.OnMyDialogResult() {
//            public void finish(Map<String, Boolean> result, List<Double> factorResult, Legs legs) {
//               // mDialogResult.finish(result, factorResult,legs);
//            }
//        });
        //dismiss();
    }

}
