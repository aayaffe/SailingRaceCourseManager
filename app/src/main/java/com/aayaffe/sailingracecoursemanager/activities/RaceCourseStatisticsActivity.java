package com.aayaffe.sailingracecoursemanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;

import java.util.List;

public class RaceCourseStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_course_statistics);
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


    }
}
