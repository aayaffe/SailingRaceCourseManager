package com.aayaffe.sailingracecoursemanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.general.Notification;
import com.aayaffe.sailingracecoursemanager.Map_Layer.GoogleMapsActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

public class ChooseEventActivity extends AppCompatActivity implements EventInputDialog.EventInputDialogListener {

    private static final String TAG = "ChooseEventActivity";
    private com.aayaffe.sailingracecoursemanager.communication.Firebase commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private static String selectedEventName;
    private Notification notification = new Notification();
    private boolean loggedIn = false;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        commManager = new com.aayaffe.sailingracecoursemanager.communication.Firebase(this);
        commManager.login(null, null, null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView eventsView = (ListView) findViewById(R.id.EventsList);
        mAdapter = new FirebaseListAdapter<Event>(this, Event.class, R.layout.three_line_list_item, commManager.getFireBaseRef().child("Events")) {
            @Override
            protected void populateView(View view, Event event, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
                String dates = getDateRangeString(event);
                User manager = commManager.findUser(event.getManagerUuid());
                if (manager==null) {
                    ((TextView) view.findViewById(android.R.id.text2)).setText("Manager: unknown");
                }
                else {
                    ((TextView) view.findViewById(android.R.id.text2)).setText("Manager: " + manager.DisplayName);
                }
                ((TextView) view.findViewById(R.id.text3)).setText(dates);
            }
        };
        eventsView.setAdapter(mAdapter);
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @NonNull
    private static String getDateRangeString(Event event) {
        if (event.monthEnd == 0 || event.dayEnd == 0 || event.yearStart == 0 || event.monthStart == 0 || event.dayStart == 0 || event.yearEnd == 0)
            return "";
        if (event.dayStart == event.dayEnd && event.monthStart == event.monthEnd && event.yearStart == event.yearEnd)
            return String.valueOf(event.dayStart) + '/' + event.monthStart + '/' + event.yearStart;
        return String.valueOf(event.dayStart) + '/' + event.monthStart + '/' + event.yearStart + " - " + event.dayEnd + '/' + event.monthEnd + '/' + event.yearEnd;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if ((auth.getCurrentUser() ==null)&&(!loggedIn)){
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setProviders(getSelectedProviders())
                            .build(),
                    RC_SIGN_IN);
        }

    }
    private String[] getSelectedProviders() {
        ArrayList<String> selectedProviders = new ArrayList<>();

        selectedProviders.add(AuthUI.EMAIL_PROVIDER);


            selectedProviders.add(AuthUI.GOOGLE_PROVIDER);


        return selectedProviders.toArray(new String[selectedProviders.size()]);
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
                DialogFragment addevent = EventInputDialog.newInstance(null, this);
                addevent.show(getFragmentManager(), "Add_Event");
                return true;

            case R.id.action_logout:
                if (loggedIn) {
                    users.logout();
                    enableLogin(true);
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setProviders(getSelectedProviders())
                                    .build(),
                            RC_SIGN_IN);
                }


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }

    }
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Logged in: " +FirebaseAuth.getInstance().getCurrentUser().getUid());
            enableLogin(false);
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Login provider error");
            Toast.makeText(this, "Login canceled",
                    Toast.LENGTH_LONG).show();
//            resetFirebaseLoginPrompt();
            return;
        }
        Toast.makeText(this, "Login Error",
                Toast.LENGTH_LONG).show();
//        showSnackbar(R.string.unknown_sign_in_response);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText eventNameText = (EditText) dialog.getDialog().findViewById(R.id.eventname);
        if ((eventNameText!=null)&&(eventNameText.getText()!=null)&&(!eventNameText.getText().toString().equals(""))){
            addEvent(eventNameText.getText().toString(),((EventInputDialog)dialog).yearStart,((EventInputDialog)dialog).yearEnd,((EventInputDialog)dialog).monthStart,((EventInputDialog)dialog).monthEnd,((EventInputDialog)dialog).dayStart,((EventInputDialog)dialog).dayEnd);
        }
        else
            Log.d(TAG, "Event not(!) created.");
            Toast t = Toast.makeText(this, "Unable to use this name",Toast.LENGTH_LONG);
            t.show();

    }

    private void addEvent(String eventNameText, int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
        //TODO: Check that user is logged in. deal with the possibilty he is not.
        Event e = new Event();
        e.setName(eventNameText);
        e.setManagerUuid(users.getCurrentUser().Uid);
        e.yearStart = yearStart;
        e.yearEnd = yearEnd;
        e.monthStart = monthStart;
        e.monthEnd = monthEnd;
        e.dayStart = dayStart;
        e.dayEnd = dayEnd;
        commManager.writeEvent(e);
    }

//    @Override
//    public void onFirebaseLoggedOut() {
//
//        Toast.makeText(this, "You have been logged out.",
//                Toast.LENGTH_SHORT).show();
//        enableLogin(true);
//    }
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
            //System.exit(0);
            finish(); // finish activity
            //System.exit(0);
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
                Log.e(TAG,"Error logging in", e);
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
                Log.e(TAG,"Error logging out.", e);
            }
            loggedIn = true;
        }
    }
}

