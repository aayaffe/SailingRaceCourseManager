package com.aayaffe.sailingracecoursemanager.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.dialogs.EventInputDialog;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.general.Notification;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;
import java.util.List;

public class ChooseEventActivity extends AppCompatActivity implements EventInputDialog.EventInputDialogListener {

    private static final String TAG = "ChooseEventActivity";
    private com.aayaffe.sailingracecoursemanager.communication.Firebase commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private static String selectedEventName;
    private Notification notification = new Notification();
    private boolean loggedIn = false;
    private static final int RC_SIGN_IN = 100;
    private Boolean exit = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        commManager = new com.aayaffe.sailingracecoursemanager.communication.Firebase(this);
        commManager.login();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView eventsView = (ListView) findViewById(R.id.EventsList);
        eventsView.setItemsCanFocus(false);
        mAdapter = new FirebaseListAdapter<Event>(this, Event.class, R.layout.three_line_list_item, commManager.getFireBaseRef().child(getString(R.string.db_events))) {
            @Override
            protected void populateView(View view, final Event event, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
                String dates = getDateRangeString(event);
                User manager = commManager.findUser(event.getManagerUuid());
                final ImageButton delete =(ImageButton)view.findViewById(R.id.delete_event_button);
                if ((manager!=null && manager.equals(users.getCurrentUser()))||users.isAdmin(users.getCurrentUser())){
                    delete.setVisibility(View.VISIBLE);
                    delete.setEnabled(true);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteEvent(event);

                        }
                    });
                }
                else {
                    delete.setVisibility(View.INVISIBLE);
                    delete.setEnabled(false);
                }
                if (manager==null) {
                    ((TextView) view.findViewById(android.R.id.text2)).setText(getString(R.string.race_officer) + ": " +getString(R.string.unknown));
                }
                else {
                    ((TextView) view.findViewById(android.R.id.text2)).setText(getString(R.string.race_officer) + ": " + manager.DisplayName);
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

    private void deleteEvent(Event event) {
        final Event e = event;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        commManager.deleteEvent(e);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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
        if (auth.getCurrentUser()!=null)
            loggedIn = true;
        if ((auth.getCurrentUser() == null)&&(!loggedIn)){
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setProviders(getSelectedProviders())
                            .setLogo(R.mipmap.banner)
                            .build(),
                    RC_SIGN_IN);
        }
    }
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        result.add(Scopes.EMAIL);
        result.add(Scopes.PROFILE);
        return result;
    }
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
        selectedProviders.add(
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                            .setPermissions(getGooglePermissions())
                            .build());
        return selectedProviders;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_event_toolbar, menu);
        if (loggedIn)
        {
            enableLogin(menu,false);
        }
        else{
            enableLogin(menu, true);
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
                    enableLogin(menu, true);
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setProviders(getSelectedProviders())
                                    .setLogo(R.mipmap.banner)
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
            enableLogin(menu, false);
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Login provider error");
            Toast.makeText(this, "Login canceled",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Login Error",
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText eventNameText = (EditText) dialog.getDialog().findViewById(R.id.eventname);
        if ((eventNameText!=null)&&(eventNameText.getText()!=null)&&(!eventNameText.getText().toString().equals(""))){
            addEvent(eventNameText.getText().toString(),((EventInputDialog)dialog).yearStart,((EventInputDialog)dialog).yearEnd,((EventInputDialog)dialog).monthStart,((EventInputDialog)dialog).monthEnd,((EventInputDialog)dialog).dayStart,((EventInputDialog)dialog).dayEnd);
        }
        else {
            Log.d(TAG, "Event not(!) created.");
            Toast t = Toast.makeText(this, "Unable to use this name", Toast.LENGTH_LONG);
            t.show();
        }

    }

    private void addEvent(String eventNameText, int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
        //TODO: Check that user is logged in. deal with the possibilty he is not.
        if  (commManager.getEvent(eventNameText)!=null){
            Log.d(TAG, "Event not(!) created. Event name exists in DB");
            Toast t = Toast.makeText(this, "Event with that name already exist.", Toast.LENGTH_LONG);
            t.show();
            return;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }


    @Override
    public void onBackPressed() {
        if (exit) {
            notification.cancelAll();
            finish();
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

    private void enableLogin(Menu menu, boolean toLogin){
        if (toLogin) {
            try {
                MenuItem logItem = menu.findItem(R.id.action_logout);//(ActionMenuItemView) findViewById(R.id.action_logout);
                if (logItem!=null) {
                    logItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login_black_48, null)); //TODO: Resize to match logout
                    logItem.setTitle("Login");
                }
                MenuItem addEventItem = menu.findItem(R.id.action_add_event);
                addEventItem.setEnabled(false);
                addEventItem.setVisible(false);
            }catch (Exception e){
                Log.e(TAG,"Error logging in", e);
            }
            loggedIn = false;
        }
        else{
            try {
                MenuItem logItem = menu.findItem(R.id.action_logout);
                logItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_logout_black_48, null));
                logItem.setTitle("Logout");
                MenuItem addEventItem = menu.findItem(R.id.action_add_event);
                addEventItem.setEnabled(true);
                addEventItem.setVisible(true);

            }catch (Exception e){
                Log.e(TAG,"Error logging out.", e);
            }
            loggedIn = true;
        }
    }
}

