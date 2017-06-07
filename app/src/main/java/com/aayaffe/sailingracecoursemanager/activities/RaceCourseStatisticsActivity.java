package com.aayaffe.sailingracecoursemanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseStatistics;

import java.util.List;

public class RaceCourseStatisticsActivity extends AppCompatActivity {
    private Legs l = null;
    private double dist2m1 = -1;
    private double windSpeed = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_course_statistics);
        Intent i = getIntent();
        l = (Legs) i.getSerializableExtra("Legs");
        dist2m1 = i.getFloatExtra("Dist2m1",-1);
        windSpeed = i.getDoubleExtra("WindSpeed",-2);
        IDBManager db = new FirebaseDB(this);
        final List<Boat> boatTypes =  db.getBoatTypes();
        ArrayAdapter<Boat> boatAdapter =
                new ArrayAdapter<Boat>(this, R.layout.simple_spinner_item_layuot, boatTypes) {
                    @Override
                    public View getView(int position,
                                        View convertView,
                                        ViewGroup parent) {
                        // Inflate only once
                        if(convertView == null) {
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.simple_spinner_item_layuot, null, false);
                        }
                        TextView tv = (TextView) convertView.findViewById(R.id.text1);
                        tv.setText(boatTypes.get(position).getBoatClass());
                        return convertView;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        // Inflate only once
                        if(convertView == null) {
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.simple_spinner_item_layuot, null, false);
                        }
                        TextView tv = (TextView) convertView.findViewById(R.id.text1);
                        tv.setText(boatTypes.get(position).getBoatClass());
                        return convertView;
                    }
                };
        Spinner s = ((Spinner)findViewById(R.id.class_spinner));
        s.setAdapter(boatAdapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Boat b = (Boat) parent.getItemAtPosition(position);
                TableLayout tl = (TableLayout) findViewById(R.id.statistics_table_layout);
                tl.removeViewsInLayout(1,tl.getChildCount()-1);
                for (MarkRoundingOrder mro : l.markRoundingOptions) {
                    TableRow tr = new TableRow(getApplicationContext());
                    TextView tv1 = new TextView(getApplicationContext());
                    tv1.setText(mro.getName());
                    TextView tv2 = new TextView(getApplicationContext());
                    tv2.setText(String.valueOf(Math.round(RaceCourseStatistics.GetSailTime(b, l, mro, dist2m1, windSpeed))));
                    tv1.setTextSize(18);
                    tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tv2.setTextSize(18);
                    tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tr.addView(tv1);
                    tr.addView(tv2);
                    tl.addView(tr);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
