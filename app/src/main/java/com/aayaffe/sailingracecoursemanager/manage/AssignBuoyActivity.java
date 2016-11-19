package com.aayaffe.sailingracecoursemanager.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;

import java.util.ArrayList;

public class AssignBuoyActivity extends AppCompatActivity {

    private static final String TAG = "AssignBuoyActivity";
    private Firebase commManager;
    private FirebaseListAdapter<Buoy> mAdapter;
    private String currentBoatName;
    private Buoy currentBoat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_buoy);
        commManager = new Firebase(this);
        Intent i = getIntent();
        currentBoatName = i.getStringExtra("boatName");
        currentBoat = commManager.getBoat(currentBoatName);
        SetupToolbar();
        ListView boatsView = (ListView) findViewById(R.id.BuoysList);
        mAdapter = new FirebaseListAdapter<Buoy>(this, Buoy.class, android.R.layout.two_line_list_item, commManager.getEventBuoysReference()) {
            @Override
            protected void populateView(View view, Buoy b, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(b.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(getAssignedBoatName(b));
            }
        };
        boatsView.setAdapter(mAdapter);
        boatsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBuoyName = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                commManager.assignBuoy(currentBoat,selectedBuoyName);
                onBackPressed();
            }
        });
    }

    private String getAssignedBoatName(Buoy b) {
        ArrayList<Buoy> boats = commManager.getAssignedBoats(b);
        if (boats==null) return "";
        if (boats.size()==0) return "";
        return boats.get(0).getName();
    }

    private void SetupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar==null) {
            Log.e(TAG,"Unable to find toolbar view.");
            return;
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Choose buoy");    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}

