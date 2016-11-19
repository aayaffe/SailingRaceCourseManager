package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.R;

import java.util.List;

/**
 * Created by Jonathan on 14/07/2016.
 */

/*
    this dialog allows user to choose the 1st leg length from 2 options
    -straightforward numeric input
    -input of boat, targetTime and wind
    those input methods are separated by 2 tabs, both produce double class output - the distance
 */
public class DistanceDialog extends Dialog {
    private Context context;
    private OnMyDialogResult mDialogResult;
    private TabHost tabHost;

    private Spinner spinner;
    private HorizontalNumberPicker windPicker;
    private HorizontalNumberPicker targetTimePicker;
    private HorizontalNumberPicker distancePicker;
    public Button finishB;

    private List<Boat> boats;  //list of boats
    private double[] courseFactors = {1,1,0};

    public DistanceDialog(Context context, List<Boat> boats) {
        super(context);
        this.context=context;
        this.boats= boats;
    }

    public DistanceDialog(Context context, List<Boat> boats ,double[] courseFactors) {
        super(context);
        this.context=context;
        this.boats= boats;
        if(courseFactors!=null)this.courseFactors=courseFactors;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.distance_dialog);

        TextView titleV=(TextView) findViewById(R.id.distance_dialog_title);   //set dialog title
        titleV.setText("Choose Distance to Mark 1");
        titleV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleV.setGravity(Gravity.CENTER);

        tabHost =(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        //first tab:
        TabHost.TabSpec spec=tabHost.newTabSpec("Distance");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Distance");
        tabHost.addTab(spec);
        //second tab:
        spec=tabHost.newTabSpec("Class & Wind");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Class & Wind");
        tabHost.addTab(spec);

        windPicker = (HorizontalNumberPicker)findViewById(R.id.wind_picker);
        windPicker.configNumbers(15, 2);
        targetTimePicker = (HorizontalNumberPicker)findViewById(R.id.target_time_picker);
        targetTimePicker.configNumbers(boats.get(0).getTargetTime(),5);
        distancePicker = (HorizontalNumberPicker)findViewById(R.id.distance_length_picker);
        distancePicker.configNumbers(1.0,0.1);
        spinner = (Spinner) findViewById(R.id.distance_class_spinner);  //NOTE: was Spinner with capital
        final String[] items = new String[boats.size()];
        for(int i=0; i<boats.size();i++){
            items[i]=boats.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                targetTimePicker.configNumbers(boats.get(position).getTargetTime(), 5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nothing. yay.
            }
        });

        finishB = (Button)findViewById(R.id.distance_finish_b);
        finishB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (tabHost.getCurrentTab()){
                    case 0:
                        mDialogResult.finish(distancePicker.getNumber());
                        break;
                    case 1:
                        Boat selectedB = boats.get(spinner.getSelectedItemPosition());
                        Toast.makeText(context, "Have you chosen "+selectedB.getName()+ "?", Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Have you chosen "+calcDistByClassWind(boats.get(spinner.getSelectedItemPosition()), windPicker.getNumber(), targetTimePicker.getNumber(), courseFactors), Toast.LENGTH_SHORT).show();
                        mDialogResult.finish(calcDistByClassWind(boats.get(spinner.getSelectedItemPosition()), windPicker.getNumber(), targetTimePicker.getNumber(), courseFactors));
                        break;
                }
                dismiss();
            }
        });
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(double result);
    }

    public double calcDistByClassWind (Boat boat, double wind, double targetTime, double[] lengthFactors){  //finds the first leg length, since it equals 1 in the factor.
        double sigmaTime= 0;
        for(int i=0;i<3;i++){
            sigmaTime = sigmaTime+(lengthFactors[i]*boat.getVmg()[wind2Index(wind)][i]);
        }
        return targetTime/sigmaTime;
    }

    public int wind2Index(double wind){  //index the wind strength. knots to right index at the boat's vmg table.
        if (wind<5) return 0;
        else if (wind<=8) return 1;
        else if (wind<=12) return 2;
        return 3;
    }
}
