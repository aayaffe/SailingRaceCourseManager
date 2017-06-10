package com.aayaffe.sailingracecoursemanager.dialogs;
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
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.DistanceType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 14/07/2016.
 */

/*
    this dialog allows user to choose the 1st leg length from 2 options
    -straightforward numeric input
    -input of boat, targetTime and wind
    those input methods are separated by 2 tabs, both produce double class output - the distance
 */
public class DistanceDialog extends Dialog {
    private static final String TAG = "DistanceDialog";
    private Legs legs;
    private Context context;
    private OnMyDialogResult mDialogResult;
    private TabHost tabHost;

    private Spinner spinner;
    private HorizontalNumberPicker windPicker;
    private HorizontalNumberPicker targetTimePicker;
    private HorizontalNumberPicker distancePicker;
    private HorizontalNumberPicker startlinelengthPicker;
    private HorizontalNumberPicker numberOfBoatsPicker;
    private HorizontalNumberPicker gateLengthPicker;
    private HorizontalNumberPicker gateBoatsLengthPicker;

    public Button finishB;
    private float factor = 1.5f; //Start line multi factor

    private List<Boat> boats;  //list of boats
    private List<Double> courseFactors = new ArrayList<>(Arrays.asList(1d,1d,0d));


    public DistanceDialog(Context context, List<Boat> boats) {
        super(context);
        this.context=context;
        this.boats= boats;
    }

    public DistanceDialog(Context context, List<Boat> boats ,Legs legs) {
        super(context);
        this.context=context;
        this.boats= boats;
        if(legs!=null)
            this.legs = legs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.distance_dialog);

        TextView titleV=(TextView) findViewById(R.id.distance_dialog_title);   //set dialog title
        titleV.setText(R.string.distance_to_mk1_label);
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
        windPicker.configNumbers(15, 2,2,50);
        targetTimePicker = (HorizontalNumberPicker)findViewById(R.id.target_time_picker);
        targetTimePicker.configNumbers(boats.get(0).getTargettime(),5,5f,400f);
        distancePicker = (HorizontalNumberPicker)findViewById(R.id.distance_length_picker);
        distancePicker.configNumbers(1.0f,0.1f,0f,15f);
        numberOfBoatsPicker = (HorizontalNumberPicker)findViewById(R.id.number_boats_picker);
        numberOfBoatsPicker.configNumbers(20.0f,1.0f,0f,100f);
        startlinelengthPicker = (HorizontalNumberPicker)findViewById(R.id.startline_length_picker);
        startlinelengthPicker.configNumbers(230f,5f,10f,1000f);
        gateLengthPicker = (HorizontalNumberPicker)findViewById(R.id.gate_length_picker);
        gateLengthPicker.configNumbers(45f,2f,10f,275f);
        gateBoatsLengthPicker = (HorizontalNumberPicker)findViewById(R.id.gate_length_boats_picker);
        gateBoatsLengthPicker.configNumbers(9,1,5,12);
        spinner = (Spinner) findViewById(R.id.distance_class_spinner);  //NOTE: was Spinner with capital
        final String[] items = new String[boats.size()];
        for(int i=0; i<boats.size();i++){
            items[i]=boats.get(i).getBoatClass();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_layout, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                targetTimePicker.configNumbers(boats.get(position).getTargettime(), 5);
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
                        mDialogResult.finish(distancePicker.getNumber(),startlinelengthPicker.getNumber(),gateLengthPicker.getNumber(),windPicker.getNumber());
                        dismiss();
                        break;
                    case 1:
                        double dist =calcDistByClassWind(boats.get(spinner.getSelectedItemPosition()), windPicker.getNumber(), targetTimePicker.getNumber(), legs);
                        if (dist<0)
                                Toast.makeText(context, "Unable to calculate course automatically!!", Toast.LENGTH_LONG).show();
                        else {
                            mDialogResult.finish(dist,
                                    calcStartLine(boats.get(spinner.getSelectedItemPosition()).length, factor, (int) numberOfBoatsPicker.getNumber()), calcGateWidth(boats.get(spinner.getSelectedItemPosition()).length, (int) gateBoatsLengthPicker.getNumber()), windPicker.getNumber());
                            dismiss();
                        }
                        break;
                }

            }
        });
    }

    private double calcGateWidth(double boatLength, int boatsNumber) {
        return boatLength*boatsNumber;
    }

    private double calcStartLine(double boatLength, float factor, int boatsNumber) {
        return boatLength*factor*boatsNumber;
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(double dist2M1, double startLineLength, double gateLength, double windSpeed);
    }

    /**
     *
     * @param b - boat class descriptor
     * @param wind - wind speed in knots
     * @param targetTime - in minutes
     * @return dist2M1 or -1 if faild
     */
    public double calcDistByClassWind (Boat b, double wind, double targetTime, Legs l){  //finds the first leg length, since it equals 1 in the factor.
        //dist2M1 = ((targetTime/speed)-AbsoluteDistance)/RelativeDistance
        if (GeneralUtils.isNull(b,l)) return -1;
        MarkRoundingOrder mro = l.defaultMarkRounding;
        if (mro==null) return -1;
        double[] Abs = new double[3];
        double[] Rel = new double[3];
        for(Boat.PointOfSail p: Boat.PointOfSail.values()) {
            try {
                double vmg = b.getVmg(wind, p);
                double length = l.GetLength(mro, p, DistanceType.Relative);
                Rel[p.ordinal()] = length * vmg;
                length = l.GetLength(mro, p, DistanceType.Absolute);
                Abs[p.ordinal()] = length * vmg;
            } catch (RaceCourseException e) {
                Log.e(TAG, "Error calculating!", e);
                return -1;
            }
        }
        Log.d(TAG,"Dist 2 M1 calculted is: "+ (targetTime-(Abs[0]+Abs[1]+Abs[2]))/(Rel[0]+Rel[1]+Rel[2]));
        return (targetTime-(Abs[0]+Abs[1]+Abs[2]))/(Rel[0]+Rel[1]+Rel[2]);
    }


}
