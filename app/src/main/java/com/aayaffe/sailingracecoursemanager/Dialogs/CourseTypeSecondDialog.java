package com.aayaffe.sailingracecoursemanager.dialogs;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;
import com.aayaffe.sailingracecoursemanager.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 13/07/2016.
 */
public class CourseTypeSecondDialog extends Dialog {
    private Context context;
    private LinearLayout ownLayout;
    private OnMyDialogResult mDialogResult;
    private RaceCourseDescriptor2 raceCourseDescriptor;
    private Button finishB;
    private List<Double> factorResult;
    private Map<String, Boolean> selectedOptions = new HashMap<>();  //map of the selected settings
    private Legs legs;


    public CourseTypeSecondDialog(Context context, RaceCourseDescriptor2 raceCourseDescriptor) {
        super(context);
        this.context = context;
        this.raceCourseDescriptor = raceCourseDescriptor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.course_type_second_dialog);

        TextView titleV=(TextView) findViewById(R.id.second_dialog_title);   //set dialog title
        titleV.setText(raceCourseDescriptor.getName() + " Course Options");
        titleV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleV.setGravity(Gravity.CENTER);


        ownLayout = (LinearLayout) findViewById(R.id.course_type_second_dialog);    //choose the layout to add the views
        addLegsSpinner((LinearLayout) findViewById(R.id.course_type_second_spinner_holder));

        finishB = new Button(context);
        finishB.setText("Done");
        finishB.setTextSize(25);
        finishB.setTextColor(ContextCompat.getColor(context,R.color.cmark_orange_lighter));
        finishB.setBackgroundResource(R.color.cmark_blue_light);
        finishB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectedOptions.put("type", raceCourseDescriptor.getName());
                for (int i = 0; i < ownLayout.getChildCount() - 1; i = i + 2) {
                    TextView tv = (TextView) ownLayout.getChildAt(i);
                    switch (ownLayout.getChildAt(i + 1).getClass().toString()) {
                        case "class android.widget.ToggleButton":
                            ToggleButton toggleButton = (ToggleButton) ownLayout.getChildAt(i + 1);
                            if (toggleButton.isChecked())
                                selectedOptions.put(tv.getText().toString().split("-")[0], true);
                            else selectedOptions.put(tv.getText().toString().split("-")[0], false);
                            break;
                    }
                }
                Log.w("check", selectedOptions.values().toString());

                mDialogResult.finish(selectedOptions, factorResult, legs);
                dismiss();
            }
        });
        redrawOptionsViews(0);

    }

    public void addLegsSpinner(LinearLayout layout){
        legs = raceCourseDescriptor.getRaceCourseLegs().get(0);
        if(raceCourseDescriptor.getRaceCourseLegs().size()>1){  //if there is only one legsType, there is no selection...
            TextView textView = new TextView(context);  //set value name on a TextView
            textView.setText("Legs");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            final Spinner dropdown = new Spinner(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_layout, raceCourseDescriptor.getLegsNames());
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
                TextView textView = new TextView(context);  //set value name on a TextView
                textView.setText(m.name + "-" +m.go.gateType.toString());
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(25);
                textView.setGravity(Gravity.CENTER);

                ownLayout.addView(textView);
                View setterBox = null;
                ToggleButton toggleB = new ToggleButton(context);
                toggleB.setTextSize(25);
                toggleB.setText("NO");
                toggleB.setTextOff("NO");
                toggleB.setTextOn("YES");
                setterBox=toggleB;
                if (setterBox != null) ownLayout.addView(setterBox);
            }
        }
        else{
            TextView textView = new TextView(context);  //set value name on a TextView
            textView.setText("No Special Options");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);

            ownLayout.addView(textView);
        }
        ownLayout.addView(finishB);
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(Map<String, Boolean> result, List<Double> factorResult, Legs legs);
    }

}
