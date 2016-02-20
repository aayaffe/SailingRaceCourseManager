package com.aayaffe.sailingracecoursemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.aayaffe.sailingracecoursemanager.map.GoogleMapsActivity;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import java.util.Random;

public class ChooseEvent extends FirebaseLoginBaseActivity {

    private com.aayaffe.sailingracecoursemanager.communication.Firebase commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private Event selectedEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView messagesView = (ListView) findViewById(R.id.EventsList);
        commManager = new com.aayaffe.sailingracecoursemanager.communication.Firebase(this);
        commManager.login(null, null, null);
        User u = new User();
        u.DisplayName = "Amit Y";
        u.Uid = "google:110163866229709027050";
        Event neu = new Event();
        neu.setName("Event2");
        neu.setEventManager(u);
        commManager.writeEvent(neu);
        mAdapter = new FirebaseListAdapter<Event>(this, Event.class, android.R.layout.two_line_list_item, commManager.getFireBaseRef().child("Events")) {
            @Override
            protected void populateView(View view, Event event, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
               // ((TextView)view.findViewById(android.R.id.text2)).setText(event.getLastBuoyId());
                view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),GoogleMapsActivity.class);
                        intent.putExtra("eventName","Event2");
                        intent.putExtra("currentEvent", selectedEvent); //TODO: Select between event name or event. get currentEvent from list selection!
                        startActivity(intent);
                    }
                });
            }



        };
        messagesView.setAdapter(mAdapter);
        users = new Users(commManager);

    }
    @Override
    protected void onStart() {
        super.onStart();

        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);

        showFirebaseLoginPrompt();
    }
    @Override
    protected Firebase getFirebaseRef() {
        return ((com.aayaffe.sailingracecoursemanager.communication.Firebase)commManager).getFireBaseRef();
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {

    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {

    }
    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        String displayName;
        try{
            displayName = authData.getProviderData().get("displayName").toString();
        }catch (Exception e){
            Random r = new Random();
            displayName = "User" + r.nextInt(10000);
        }
        users.setCurrentUser(authData.getUid(), displayName);

    }

    @Override
    public void onFirebaseLoggedOut() {
        // TODO: Handle logout
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
}
