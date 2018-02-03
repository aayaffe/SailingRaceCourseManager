package com.aayaffe.sailingracecoursemanager.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ListView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.adapters.EventsListAdapter;
import com.aayaffe.sailingracecoursemanager.BuildConfig;
import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.dialogs.EventInputDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.OneTimeAlertDialog;
import com.aayaffe.sailingracecoursemanager.general.Analytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.tenmiles.helpstack.HSHelpStack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.doorbell.android.Doorbell;

public class ChooseEventActivity extends AppCompatActivity implements EventInputDialog.EventInputDialogListener {

    private static final String TAG = "ChooseEventActivity";
    private FirebaseDB commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private  Event selectedEvent;
    private Analytics analytics;
    private boolean loggedIn = false;
    private static final int RC_SIGN_IN = 100;
    private Menu menu;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        analytics = new Analytics(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        commManager = FirebaseDB.getInstance(this);
        //commManager.login();
        Users.Init(commManager,sharedPreferences);
        users = Users.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView eventsView = (ListView) findViewById(R.id.EventsList);
        eventsView.setItemsCanFocus(false);

        mAdapter = new EventsListAdapter(this, Event.class, R.layout.three_line_list_item,
                commManager.getFireBaseRef().child(getString(R.string.db_events)),commManager,users);
        eventsView.setAdapter(mAdapter);
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e  = (Event)parent.getItemAtPosition(position);
                enterEvent(false,e);
            }
        });
        showRecentUpdateOnce(this);
    }


    private void enterEvent(boolean viewOnly,Event e){
        Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
        selectedEvent = e;
        commManager.setCurrentEvent(selectedEvent);
        intent.putExtra("eventName", selectedEvent.getName());
        intent.putExtra("viewOnly", viewOnly);
        startActivity(intent);
    }
    /** Show the recent updates prompt once per version. */
    public static void showRecentUpdateOnce(Activity activity) {
        new OneTimeAlertDialog.Builder(activity, "recent_updates_dialog" + BuildConfig.VERSION_NAME)
                .setTitle(activity.getString(R.string.disclaimer_title))
                .setMessage(activity.getString(R.string.disclaimer_message))
                .show();
    }

    public void deleteEvent(Event event) {
        final Event e = event;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        commManager.deleteEvent(e);
                        analytics.LogDeleteEvent(e,users.getCurrentUser());
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                    default:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null)
            loggedIn = true;
        if ((auth.getCurrentUser() == null)&&(!loggedIn)){
            startLoginActivity();
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
        menu.clear();
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
                Log.d(TAG, "Add event pressed");
                DialogFragment addevent = EventInputDialog.newInstance(null, this);
                addevent.show(getFragmentManager(), "Add_Event");
                return true;
            case R.id.action_get_help:
                Log.d(TAG, "Get help pressed");
                HSHelpStack.getInstance(this).showHelp(this);
                return true;
            case R.id.action_feedback:
                Log.d(TAG, "Give feedback pressed");
//                if(BuildConfig.DEBUG){
//                    throw new RuntimeException("For testing purposes only");
//                }
                new Doorbell(this, 5756, getString(R.string.doorbellioKey)).show();

                return true;
            case R.id.action_logout:
                if (loggedIn) {
                    Users.logout();
                    enableLogin(menu, true);
                }
                else startLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    


    private void startLoginActivity() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setProviders(getSelectedProviders())
                        .setLogo(R.mipmap.banner)
                        .setTosUrl("http://aayaffe.github.io/SailingRaceCourseManager/Privacy%20Policy.html")
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode);
        }

    }
    private void handleSignInResponse(int resultCode) {
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


    /**
     * Event add dialog positive click listener
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText eventNameText = (EditText) dialog.getDialog().findViewById(R.id.eventname);
        if ((eventNameText!=null)&&(eventNameText.getText()!=null)&&(!eventNameText.getText().toString().isEmpty())){
            if (!eventNameText.getText().toString().matches(".*[\\.\\#\\$\\[\\]]+.*"))
                addEvent(eventNameText.getText().toString(),((EventInputDialog)dialog).yearStart,
                        ((EventInputDialog)dialog).yearEnd, ((EventInputDialog)dialog).monthStart,
                        ((EventInputDialog)dialog).monthEnd,((EventInputDialog)dialog).dayStart,
                        ((EventInputDialog)dialog).dayEnd);
        }
        else {
            Log.d(TAG, "Event not(!) created.");
            Toast t = Toast.makeText(this, R.string.new_event_name_not_accepted_toast_message, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private void addEvent(String eventNameText, int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
        if (users.getCurrentUser()!=null) {
            if (commManager.getEvent(eventNameText) != null) {
                Log.d(TAG, "Event not(!) created. Event name exists in DB");
                Toast t = Toast.makeText(this, R.string.new_event_duplicate_name_toast_message, Toast.LENGTH_LONG);
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
            Calendar start = Calendar.getInstance();
            start.set(yearStart,monthStart,dayStart);
            Calendar end = Calendar.getInstance();
            end.set(yearEnd,monthEnd,dayEnd);
            analytics.LogAddEvent(e.getName(),start.getTime(),end.getTime(),users.getCurrentUser());
        }
        else {
            FirebaseCrash.logcat(Log.DEBUG, TAG,"User not logged in tried to add new activity");
            FirebaseCrash.report(new Exception("User not logged in tried to add new activity"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    private void enableLogin(Menu menu, boolean toLogin){
        if (toLogin) {
            try {
                MenuItem logItem = menu.findItem(R.id.action_logout);
                if (logItem!=null) {
                    logItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login_black_48, null)); //TODO: Resize to match logout
                    logItem.setTitle("Login");
                }
                MenuItem addEventItem = menu.findItem(R.id.action_add_event);
                addEventItem.setEnabled(false);
                addEventItem.setVisible(false);
                startLoginActivity();
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

    public void viewOnly(Event event) {
        enterEvent(true, event);
    }
}

