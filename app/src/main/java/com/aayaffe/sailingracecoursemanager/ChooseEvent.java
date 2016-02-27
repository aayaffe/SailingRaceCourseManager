package com.aayaffe.sailingracecoursemanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.general.Notification;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.aayaffe.sailingracecoursemanager.map.GoogleMapsActivity;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import java.util.Random;

public class ChooseEvent extends FirebaseLoginBaseActivity implements EventInputDialog.EventInputDialogListener {

    private static final String TAG = "ChooseEvent";
    private com.aayaffe.sailingracecoursemanager.communication.Firebase commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private Event selectedEvent;
    private String selectedEventName;
    private DialogFragment addevent;
    private Notification notification = new Notification();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ListView messagesView = (ListView) findViewById(R.id.EventsList);
        commManager = new com.aayaffe.sailingracecoursemanager.communication.Firebase(this);
        commManager.login(null, null, null);
//        User u = new User();
//        u.DisplayName = "Amit H";
//        u.Uid = "google:110163866229709027050";
//        Event neu = new Event();
//        neu.setName("Event3");
//        neu.setEventManager(u);
//        commManager.writeEvent(neu);

        mAdapter = new FirebaseListAdapter<Event>(this, Event.class, android.R.layout.two_line_list_item, commManager.getFireBaseRef().child("Events")) {
            @Override
            protected void populateView(View view, Event event, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText("Manager: "+ event.getEventManager().DisplayName);
            }
        };
        messagesView.setAdapter(mAdapter);
        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
                selectedEventName = ((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                commManager.setCurrentEventName(selectedEventName);
                intent.putExtra("eventName", selectedEventName);
                intent.putExtra("currentEvent", selectedEvent); //TODO: Select between event name or event. get currentEvent from list selection!
                startActivity(intent);
            }
        });
        users = new Users(commManager);
        notification.InitNotification(this);


    }
    @Override
    protected void onStart() {
        super.onStart();

        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
        if (commManager.getLoggedInUid()==null){
            showFirebaseLoginPrompt();
        }
    }
    @Override
    protected Firebase getFirebaseRef() {
        return ((com.aayaffe.sailingracecoursemanager.communication.Firebase)commManager).getFireBaseRef();
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
        //TODO: Handle correctly
    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
        //TODO: Handle correctly
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
    public void onLogoutButtonClick(View v){
        users.logout();
    }
    public void onAddEventButtonClick(View v){
        Log.d(TAG, "Plus Fab Clicked");
        addevent = EventInputDialog.newInstance(null);
        addevent.show(getFragmentManager(), "Add_Event");
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText eventNameText = (EditText) dialog.getDialog().findViewById(R.id.eventname);
        if ((eventNameText!=null)||(eventNameText.getText().toString()!="")){
            addEvent(eventNameText.getText().toString());
        }
        else
            Log.d(TAG, "Event not(!) created.");
    }

    private void addEvent(String eventNameText) {
        //TODO: Check that user is logged in. deal with the possibilty he is not.
        Event e = new Event();
        e.setName(eventNameText);
        e.setEventManager(users.getCurrentUser());
        commManager.writeEvent(e);
    }

    @Override
    public void onFirebaseLoggedOut() {
        // TODO: Handle logout - currently handeled in FireBase class
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }




    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            //mBuilder.setOngoing(false);
            notification.cancelAll();
            finish(); // finish activity
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
