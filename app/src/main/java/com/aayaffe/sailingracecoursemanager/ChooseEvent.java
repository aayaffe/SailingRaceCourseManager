package com.aayaffe.sailingracecoursemanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.general.Notification;
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
    private static String selectedEventName;
    private DialogFragment addevent;
    private Notification notification = new Notification();
    private boolean loggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        commManager = new com.aayaffe.sailingracecoursemanager.communication.Firebase(this);
        commManager.login(null, null, null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView messagesView = (ListView) findViewById(R.id.EventsList);
        mAdapter = new FirebaseListAdapter<Event>(this, Event.class, android.R.layout.two_line_list_item, commManager.getFireBaseRef().child("Events")) {
            @Override
            protected void populateView(View view, Event event, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
                User manager = event.getEventManager();
                if (manager==null) {
                    ((TextView) view.findViewById(android.R.id.text2)).setText("Manager: unknown");
                }
                else {
                    ((TextView) view.findViewById(android.R.id.text2)).setText("Manager: " + event.getEventManager().DisplayName);
                }
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
        if ((commManager.getFireBaseRef().getAuth()==null)&&(!loggedIn)){
            showFirebaseLoginPrompt();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_event_toolbar, menu);
        if (loggedIn)
        {
            enableLogin(false);
        }
        else{
            enableLogin(true);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_event:
                Log.d(TAG, "Plus Fab Clicked");
                addevent = EventInputDialog.newInstance(null);
                addevent.show(getFragmentManager(), "Add_Event");
                return true;

            case R.id.action_logout:
                if (loggedIn) {
                    users.logout();
                    enableLogin(true);
                }
                else{
                    showFirebaseLoginPrompt();
                }


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected Firebase getFirebaseRef() {
        return commManager.getFireBaseRef();
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
        //TODO: Handle correctly
        Log.d(TAG, "Login provider error: " + firebaseLoginError.message);
        Toast.makeText(this, firebaseLoginError.message,
                Toast.LENGTH_LONG).show();
        resetFirebaseLoginPrompt();
    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
        //TODO: Handle correctly
        Log.d(TAG, "Login User error: " + firebaseLoginError.message);
        Toast.makeText(this, firebaseLoginError.message,Toast.LENGTH_LONG).show();
        resetFirebaseLoginPrompt();
    }
    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        Log.d(TAG, "Logged in: " +authData.getUid());
        String displayName;
        enableLogin(false);
        try{
            displayName = authData.getProviderData().get("displayName").toString();
        }catch (Exception e){
            Random r = new Random();
            displayName = "User" + r.nextInt(10000);
        }
        //users.setCurrentUser(authData.getUid(), displayName);

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

        Toast.makeText(this, "You have been logged out.",
                Toast.LENGTH_SHORT).show();
        enableLogin(true);
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

    private void enableLogin(boolean toLogin){
        if (toLogin) {

            try {
                ActionMenuItemView log_item = (ActionMenuItemView) findViewById(R.id.action_logout);
                log_item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login_black_48, null)); //TODO: Resize to match logout
                log_item.setTitle("Login");
                ActionMenuItemView add_event_item = (ActionMenuItemView) findViewById(R.id.action_add_event);
                add_event_item.setEnabled(false);
            }catch (Exception e){

            }
            loggedIn = false;
        }
        else{
            try {
                ActionMenuItemView log_item = (ActionMenuItemView) findViewById(R.id.action_logout);
                log_item.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_logout_black_48, null));
                log_item.setTitle("Logout");
                ActionMenuItemView add_event_item = (ActionMenuItemView) findViewById(R.id.action_add_event);
                add_event_item.setEnabled(true);

            }catch (Exception e){

            }
            loggedIn = true;
        }
    }
}

