package com.aayaffe.sailingracecoursemanager.Steps;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
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

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 11/03/2017.
 */

public class SelectCourseOptionsStep extends ButterKnifeFragment implements Step, CourseTypeRV.OnRecyclerItemClickListener {
    private static final String CLICKS_KEY = "clicks";

    private static final int TAP_THRESHOLD = 2;

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";

    private int i = 0;
    private LinearLayout ownLayout;
    private CourseTypeSecondDialog.OnMyDialogResult mDialogResult;
    private RaceCourseDescriptor2 raceCourseDescriptor;
    private Button finishB;
    private List<Double> factorResult;
    private Map<String, Boolean> selectedOptions = new HashMap<>();  //map of the selected settings
    private Legs legs;

    public static SelectCourseOptionsStep newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        SelectCourseOptionsStep fragment = new SelectCourseOptionsStep();
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
    public void addLegsSpinner(LinearLayout layout){
        legs = raceCourseDescriptor.getRaceCourseLegs().get(0);
        if(raceCourseDescriptor.getRaceCourseLegs().size()>1){  //if there is only one legsType, there is no selection...
            TextView textView = new TextView(getContext());  //set value name on a TextView
            textView.setText("Legs");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            final Spinner dropdown = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_layout, raceCourseDescriptor.getLegsNames());
            dropdown.setAdapter(adapter);
            dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER);
            layout.addView(dropdown);

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    redrawOptionsViews(position);
                    factorResult = raceCourseDescriptor.getRaceCourseLegs().get(position).getCourseFactors();
                    legs = raceCourseDescriptor.getRaceCourseLegs().get(position);
                    //selectedOptions.put("Legs", raceCourseDescriptor.getLegsNames()[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
    }

    public void redrawOptionsViews(int legsIndex){
        ownLayout.removeAllViews();
        if(raceCourseDescriptor.getRaceCourseLegs().size()>0 && raceCourseDescriptor.getRaceCourseLegs().get(legsIndex).getOptions().size()>0){
            List<Mark2> options = raceCourseDescriptor.getRaceCourseLegs().get(legsIndex).getOptions();
            for (Mark2 m : options) {  //add all the race course options views. textView for the name and Spinner/Toggle/... for value
                TextView textView = new TextView(getContext());  //set value name on a TextView
                textView.setText(m.name + "-" +m.go.gateType.toString());
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(25);
                textView.setGravity(Gravity.CENTER);

                ownLayout.addView(textView);
                View setterBox = null;
                ToggleButton toggleB = new ToggleButton(getContext());
                toggleB.setTextSize(25);
                toggleB.setText("NO");
                toggleB.setTextOff("NO");
                toggleB.setTextOn("YES");
                setterBox=toggleB;
                if (setterBox != null) ownLayout.addView(setterBox);
            }
        }
        else{
            TextView textView = new TextView(getContext());  //set value name on a TextView
            textView.setText("No Special Options");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);

            ownLayout.addView(textView);
        }
        ownLayout.addView(finishB);
    }
}
