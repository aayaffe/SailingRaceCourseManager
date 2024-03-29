package com.aayaffe.sailingracecoursemanager.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;

import java.util.List;

public class ChooseBoatActivity extends AppCompatActivity {

    private static final String TAG = "ChooseBoatActivity";
    private FirebaseDB commManager;
    private FirebaseListAdapter<DBObject> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_boat);
        commManager = FirebaseDB.getInstance(this);
        setupToolbar();
        ListView boatsView = (ListView) findViewById(R.id.BoatsList);
        FirebaseListOptions<DBObject> options = new FirebaseListOptions.Builder<DBObject>()
                .setLayout(R.layout.two_line_with_action_icon_list_item)
                .setQuery(commManager.getEventBoatsReference(), DBObject.class)
                .build();
        mAdapter = new FirebaseListAdapter<DBObject>(options) {
            @Override
            protected void populateView(View view, final DBObject b, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText("Boat: " + b.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText("Assigned to: "+getAssignedBuoysNames(b));
                final ImageButton remove =(ImageButton)view.findViewById(R.id.remove_assignment_button);
                List<DBObject> assigned = commManager.getAssignedBuoys(b);
                if (assigned!=null&& !assigned.isEmpty())
                {
                    remove.setVisibility(View.VISIBLE);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeAssignment(b);
                            remove.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };
        mAdapter.startListening();
        boatsView.setAdapter(mAdapter);
        boatsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AssignBuoyActivity.class);
                DBObject boat = (DBObject) parent.getItemAtPosition(position);
                //String selectedBoatName = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                intent.putExtra("boatUid", boat.userUid);
                startActivity(intent);
            }
        });
    }
    private void removeAssignment(DBObject b) {
        commManager.removeAssignments(b);
    }

    private String getAssignedBuoysNames(DBObject b) {
        List<DBObject> buoys = commManager.getAssignedBuoys(b);
        String ret = "";
        StringBuilder sb = new StringBuilder();
        for(DBObject buoy:buoys){
            ret = sb.append(buoy.getName())/*.append(",")*/.toString();
        }
        return ret;
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
        getSupportActionBar().setTitle("Choose boat");
        toolbar.setTitle("Choose boat");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.stopListening();
    }
}

