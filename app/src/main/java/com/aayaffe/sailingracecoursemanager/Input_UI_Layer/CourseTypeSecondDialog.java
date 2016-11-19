package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseType;
import com.aayaffe.sailingracecoursemanager.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 13/07/2016.
 */
public class CourseTypeSecondDialog extends Dialog {
    private Context context;
    private LinearLayout ownLayout;
    private OnMyDialogResult mDialogResult;
    private CourseType courseType;
    private Button finishB;
    private double[] factorResult;
    private Map<String, String> selectedOptions = new HashMap<String, String>();  //map of the selected settings



    public CourseTypeSecondDialog(Context context, CourseType courseType) {
        super(context);
        this.context = context;
        this.courseType=courseType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.course_type_second_dialog);

        TextView titleV=(TextView) findViewById(R.id.second_dialog_title);   //set dialog title
        titleV.setText(courseType.getName() + " Course Options");
        titleV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleV.setGravity(Gravity.CENTER);


        ownLayout = (LinearLayout) findViewById(R.id.course_type_second_dialog);    //choose the layout to add the views
        addLegsSpinner((LinearLayout) findViewById(R.id.course_type_second_spinner_holder));

        finishB = new Button(context);
        finishB.setText("Done");
        finishB.setTextSize(25);
        finishB.setTextColor(context.getResources().getColor(R.color.cmark_orange_lighter));
        finishB.setBackgroundResource(R.color.cmark_blue_light);
        finishB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOptions.put("type", courseType.getName());
                for (int i = 0; i < ownLayout.getChildCount() - 1; i = i + 2) {
                    TextView tv = (TextView) ownLayout.getChildAt(i);
                    switch (ownLayout.getChildAt(i + 1).getClass().toString()) {
                        case "class android.widget.Spinner":
                            Toast.makeText(context, "we can work from home! oooh o-oh", Toast.LENGTH_LONG).show();
                            Spinner spinner = (Spinner) ownLayout.getChildAt(i + 1);
                            selectedOptions.put(tv.getText().toString(), spinner.getSelectedItem().toString());
                            break;
                        case "class android.widget.ToggleButton":
                            ToggleButton toggleButton = (ToggleButton) ownLayout.getChildAt(i + 1);
                            if (toggleButton.isChecked())
                                selectedOptions.put(tv.getText().toString(), "true");
                            else selectedOptions.put(tv.getText().toString(), "false");
                            break;
                    }
                }
                Log.w("check", selectedOptions.values().toString());

                mDialogResult.finish(selectedOptions, factorResult);
                dismiss();
            }
        });
        redrawOptionsViews(0);

    }

    public void addLegsSpinner(LinearLayout layout){
        if(courseType.getLegsTypes().size()>1){  //if there is only one legsType, there is no selection...
            TextView textView = new TextView(context);  //set value name on a TextView
            textView.setText("Legs");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            final Spinner dropdown = new Spinner(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, courseType.getLegsNames());
            dropdown.setAdapter(adapter);
            dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER);

            layout.addView(dropdown);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    redrawOptionsViews(position);
                    factorResult=courseType.getLegsTypes().get(position).getCourseFactors();
                    selectedOptions.put("Legs", courseType.getLegsNames()[position]);
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
        if(courseType.getLegsTypes().size()>0 && courseType.getLegsTypes().get(legsIndex).getOptions().size()>0){
            List<String[]> options = courseType.getLegsTypes().get(legsIndex).getOptions();
            for (int c = 0; c < options.size(); c++) {  //add all the race course options views. textView for the name and Spinner/Toggle/... for value
                TextView textView = new TextView(context);  //set value name on a TextView
                textView.setText(options.get(c)[0]);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(25);
                textView.setGravity(Gravity.CENTER);

                ownLayout.addView(textView);
                View setterBox = null;
                switch (options.get(c)[1]) {   //set an input view to get the value from the user
                    case "spinner":
                        Spinner dropdown = new Spinner(context);
                        String[] items = Arrays.copyOfRange(options.get(c), 2, options.get(c).length);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, items);
                        dropdown.setAdapter(adapter);
                        dropdown.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setGravity(Gravity.CENTER);

                        setterBox=dropdown;
                        break;
                    case "toggle":
                        ToggleButton toggleB = new ToggleButton(context);
                        toggleB.setTextSize(25);
                        toggleB.setText("NO");
                        toggleB.setTextOff("NO");
                        toggleB.setTextOn("YES");
                        setterBox=toggleB;
                        break;
                }
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
        void finish(Map<String, String> result, double[] factorResult);
    }

}
