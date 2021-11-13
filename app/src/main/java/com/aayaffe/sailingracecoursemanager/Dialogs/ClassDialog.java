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
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.adapters.OrcCertListAdapter;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import in.avimarine.orccertificatesimporter.CountryObj;
import in.avimarine.orccertificatesimporter.ORCCertObj;
import in.avimarine.orccertificatesimporter.ORCCertsImporter;

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
public class ClassDialog extends Dialog {
    private static final String TAG = "ClassDialog";
    private Legs legs;
    private Context context;
    private OnMyDialogResult mDialogResult;
    private Spinner yachtSpinner;



    public Button finishB;

    private List<Boat> boats;  //list of boats
    private List<Double> courseFactors = new ArrayList<>(Arrays.asList(1d,1d,0d));
    private ORCCertObj selectedYacht;


    public ClassDialog(Context context, List<Boat> boats , Legs legs) {
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
        super.setContentView(R.layout.dialog_choose_class);

        TextView titleV=(TextView) findViewById(R.id.class_dialog_title);   //set dialog title
        titleV.setText(R.string.select_class);
        titleV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleV.setGravity(Gravity.CENTER);


        setClassSpinner();
        setYachtList();
        finishB = (Button)findViewById(R.id.distance_finish_b);
        finishB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                switch (tabHost.getCurrentTab()){
//                    case 0:
//                        mDialogResult.finish(distancePicker.getNumber(),startlinelengthPicker.getNumber(),gateLengthPicker.getNumber(),windPicker.getNumber());
//                        dismiss();
//                        break;
//                    case 1:
//                        double dist =calcDistByClassWind(boats.get(spinner.getSelectedItemPosition()), windPicker.getNumber(), targetTimePicker.getNumber(), legs);
//                        if (dist<0)
//                                Toast.makeText(context, "Unable to calculate course automatically!!", Toast.LENGTH_LONG).show();
//                        else {
//                            mDialogResult.finish(dist,
//                                    calcStartLine(boats.get(spinner.getSelectedItemPosition()).length, factor, (int) numberOfBoatsPicker.getNumber()), calcGateWidth(boats.get(spinner.getSelectedItemPosition()).length, (int) gateBoatsLengthPicker.getNumber()), windPicker.getNumber());
//                            dismiss();
//                        }
//                        break;
//                    default:
//                        break;
//                }

            }
        });
    }

    private void setClassSpinner() {
        final ArrayList<String> items = new ArrayList<>();
        for(int i=0; i<boats.size();i++){
            items.add(boats.get(i).getBoatClass());
        }
        items.add("ORC");
        Spinner classSpinner = (Spinner) findViewById(R.id.class_class_spinner);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, items);
        classSpinner.setAdapter(classAdapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if ("ORC".equals(items.get(position))){
                    setCountrySpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nothing. yay.
            }
        });
    }

    private void setCountrySpinner() {
        final ArrayList<String> items = new ArrayList<>();
        Spinner countrySpinner = (Spinner) findViewById(R.id.class_country_spinner);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, items);
        countrySpinner.setAdapter(classAdapter);
        ORCCertsImporter.getCountries(ret -> {
            Log.d(TAG, "Got countries. " + ret.size());
            for(CountryObj co: ret){
                items.add(co.getId());
                classAdapter.notifyDataSetChanged();
            }
        });



        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String countryCode = items.get(position);
                setYachtSpinner(countryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nothing. yay.
            }
        });
    }

    private void setYachtSpinner(String countryCode) {
        final ArrayList<String> items = new ArrayList<>();
        final ArrayList<ORCCertObj> certs = new ArrayList<>();
        yachtSpinner = (Spinner) findViewById(R.id.class_boat_spinner);
        ArrayAdapter<String> yachtAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, items);
        yachtSpinner.setAdapter(yachtAdapter);
        ORCCertsImporter.getCertsByCountry(countryCode, ret -> {
            certs.clear();
            certs.addAll(ret);
            sortByCountryName(certs);
            for(ORCCertObj co: certs){
                items.add(co.getYachtsName());
                yachtAdapter.notifyDataSetChanged();
            }
        });
        yachtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedYacht = certs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Nothing. yay.
            }
        });
    }
    private void setYachtList() {

        ListView yachtListView = findViewById(R.id.class_yacht_list);

        OrcCertListAdapter mAdapter = new OrcCertListAdapter(getContext());
        yachtListView.setAdapter((ListAdapter) mAdapter);

        ImageButton b = findViewById(R.id.class_add_yacht_button);

        b.setOnClickListener(view -> {
            mAdapter.addItem(selectedYacht);
        });
    }


    private void sortByCountryName(ArrayList<ORCCertObj> certs) {
        Collections.sort(certs, (o1, o2) -> o1.getYachtsName().compareTo(o2.getYachtsName()));
    }


    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(double dist2M1, double startLineLength, double gateLength, double windSpeed);
    }



}
