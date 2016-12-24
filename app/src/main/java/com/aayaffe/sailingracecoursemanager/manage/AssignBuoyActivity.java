package com.aayaffe.sailingracecoursemanager.Manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;

import java.util.ArrayList;

public class AssignBuoyActivity extends AppCompatActivity {

    private static final String TAG = "AssignBuoyActivity";
    private Firebase commManager;
    private FirebaseListAdapter<DBObject> mAdapter;
    private DBObject currentBoat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_buoy);
        commManager = new Firebase(this);
        Intent i = getIntent();
        currentBoat = commManager.getBoat(i.getStringExtra("boatName"));
        setupToolbar();
        ListView boatsView = (ListView) findViewById(R.id.BuoysList);
        mAdapter = new FirebaseListAdapter<DBObject>(this, DBObject.class, android.R.layout.two_line_list_item, commManager.getEventBuoysReference()) {
            @Override
            protected void populateView(View view, DBObject b, int position) {
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

    private String getAssignedBoatName(DBObject b) {
        ArrayList<DBObject> boats = commManager.getAssignedBoats(b);
        if (boats==null)
            return "";
        if (boats.size()==0)
            return "";
        if (boats.get(0)!=null)
            return boats.get(0).getName();
        return null;
    }

    private void setupToolbar() {
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
        getSupportActionBar().setTitle("Choose buoy");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}

