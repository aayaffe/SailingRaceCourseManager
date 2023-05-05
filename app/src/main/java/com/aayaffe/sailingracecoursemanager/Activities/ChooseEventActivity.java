package com.aayaffe.sailingracecoursemanager.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.BuildConfig;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.adapters.EventsListAdapter;
import com.aayaffe.sailingracecoursemanager.db.FeatureFlags;
import com.aayaffe.sailingracecoursemanager.db.FirebaseBackgroundService;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.dialogs.AccessCodeInputDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.AccessCodeShowDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.EventInputDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.OneTimeAlertDialog;
import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.general.Analytics;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
//import com.tenmiles.helpstack.HSHelpStack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.doorbell.android.Doorbell;

public class ChooseEventActivity extends AppCompatActivity implements EventInputDialog.EventInputDialogListener, AccessCodeInputDialog.AccessCodeInputDialogListener {

    private static final String TAG = "ChooseEventActivity";
    private static final int RC_SIGN_IN = 100;
    private FirebaseDB commManager;
    private FirebaseListAdapter<Event> mAdapter;
    private Users users;
    private Event selectedEvent;
    private Analytics analytics;
    private boolean loggedIn = false;
    private Menu menu;
    private SharedPreferences sharedPreferences;
    private DialogFragment df;
    static final int PROMINENT_DISCLOSURE_ACTIVITY_REQUEST = 9547;  // The request code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        commManager = FirebaseDB.getInstance(this);
        Users.Init(commManager, sharedPreferences);
        users = Users.getInstance();
        if (users.getCurrentUser() != null) {
            analytics = new Analytics(this, users.getCurrentUser().Uid, users.isAdmin(users.getCurrentUser()));
        }
        FeatureFlags featureFlags = new FeatureFlags();
        Log.d(TAG, "bluetooth_race_horn = " + featureFlags.getFlag("bluetooth_race_horn"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView eventsView = findViewById(R.id.EventsList);
        eventsView.setItemsCanFocus(false);
        FirebaseListOptions<Event> options = new FirebaseListOptions.Builder<Event>()
                .setLayout(R.layout.three_line_list_item)
                .setQuery(commManager.getFireBaseRef().child(getString(R.string.db_events)), Event.class)
                .build();
        mAdapter = new EventsListAdapter(options, this, commManager, users);
        mAdapter.startListening();
        eventsView.setAdapter(mAdapter);
        eventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = (Event) parent.getItemAtPosition(position);
                eventPressed(false, e);
            }
        });
        showRecentUpdateOnce(this);
    }


    private void eventPressed(boolean viewOnly, Event e) {
        selectedEvent = e;
        if (selectedEvent.accessCode == null || selectedEvent.accessCode.isEmpty()) {
            enterEvent(viewOnly);
        } else if (viewOnly || selectedEvent.getManagerUuid().equals(commManager.getLoggedInUid()) || (selectedEvent.getBoats()!=null && selectedEvent.getBoats().containsKey(commManager.getLoggedInUid()))) {
            enterEvent(viewOnly);
        } else {
            df = AccessCodeInputDialog.newInstance(this);
            df.show(getFragmentManager(), "Enter_Access_Code");
        }
    }

    /**
     * Show the recent updates prompt once per version.
     */
    public static void showRecentUpdateOnce(Activity activity) {
        new OneTimeAlertDialog.Builder(activity, "recent_updates_dialog" + BuildConfig.VERSION_NAME)
                .setTitle(activity.getString(R.string.disclaimer_title))
                .setMessage(activity.getString(R.string.disclaimer_message))
                .show();
    }

    private void enterEvent(boolean viewOnly) {
        Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
        commManager.setCurrentEvent(selectedEvent);
        intent.putExtra("eventName", selectedEvent.getName());
        intent.putExtra("viewOnly", viewOnly);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"On Start");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ListView eventsView = findViewById(R.id.EventsList);
        if (auth.getCurrentUser()!=null){
            eventsView.setVisibility(View.VISIBLE);
        }
        else {
            eventsView.setVisibility(View.INVISIBLE);
        }
        if (auth.getCurrentUser() != null)
            loggedIn = true;
        if ((auth.getCurrentUser() == null) && (!loggedIn)) {
            startLoginActivity();
        }
        if (analytics==null && users.getCurrentUser()!= null){
            analytics = new Analytics(this, users.getCurrentUser().Uid, users.isAdmin(users.getCurrentUser()));
        }
    }

    private void startLoginActivity() {
        Log.d(TAG, "Starting login activity");
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getSelectedProviders())
                        .setPrivacyPolicyUrl("http://sailrace.avimarine.in/privacypolicy.html")
                        .setLogo(R.mipmap.banner)
                        .build(),
                RC_SIGN_IN);
    }

    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
        selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder()
                .setRequireName(true)
                .setAllowNewAccounts(true)
                .build());
        selectedProviders.add(new AuthUI.IdpConfig.GoogleBuilder().build());
        return selectedProviders;
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, FirebaseBackgroundService.class));
        mAdapter.stopListening();
        super.onDestroy();
    }

    /**
     * enter access code click
     * @param dialog
     */
    @Override
    public void onAccessCodeDialogPositiveClick(DialogFragment dialog) {
        EditText accessCodeText = (EditText) dialog.getDialog().findViewById(R.id.access_code);
        if (accessCodeText == null)
            return;
        if (accessCodeText.getText() == null)
            return;
        if (GeneralUtils.isValid(accessCodeText.getText().toString(), Long.class, 0f, 999999f) && selectedEvent.accessCode.equals(accessCodeText.getText().toString())) {
            enterEvent(false);
        }
    }

    public void deleteEvent(Event event) {
        final Event e = event;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        commManager.deleteEvent(e);
                        analytics.LogDeleteEvent(e, users.getCurrentUser());
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_event_toolbar, menu);
        if (loggedIn) {
            enableLogin(menu, false);
        } else {
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
//                HSHelpStack.getInstance(this).showHelp(this);
                return true;
            case R.id.action_feedback:
                Log.d(TAG, "Give feedback pressed");
                new Doorbell(this, 5756, getString(R.string.doorbellioKey)).show();
                return true;
            case R.id.action_logout:
//                if (loggedIn) {
                    logout();
//                } else startLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Users.logout();
        enableLogin(menu, true);
        startLoginActivity();
    }

    private void enableLogin(Menu menu, boolean toLogin) {
        if (toLogin) {
            try {
                MenuItem logItem = menu.findItem(R.id.action_logout);
                if (logItem != null) {
                    logItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login_black_48, null));
                    logItem.setTitle("Login");
                }
                MenuItem addEventItem = menu.findItem(R.id.action_add_event);
                addEventItem.setEnabled(false);
                addEventItem.setVisible(false);
            } catch (Exception e) {
                Log.e(TAG, "Error logging in", e);
            }
            loggedIn = false;
        } else {
            try {
                MenuItem logItem = menu.findItem(R.id.action_logout);
                logItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_logout_black_48, null));
                logItem.setTitle("Logout");
                MenuItem addEventItem = menu.findItem(R.id.action_add_event);
                addEventItem.setEnabled(true);
                addEventItem.setVisible(true);
            } catch (Exception e) {
                Log.e(TAG, "Error logging out.", e);
            }
            loggedIn = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode);
        }
        else if (requestCode == PROMINENT_DISCLOSURE_ACTIVITY_REQUEST){
            handleProminentDisclosureResult(resultCode);
        }
    }



    private void handleSignInResponse(int resultCode) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Logged in: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            startProminentDisclosureActivity();
            enableLogin(menu, false);
            if (mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "Login provider error");
            Toast.makeText(this, "Login canceled",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Toast.makeText(this, "Login Error",
                Toast.LENGTH_LONG).show();
    }

    /**
     * This method will start the Privacy Policy prominent disclosure Activity.
     */
    private void startProminentDisclosureActivity() {
        Intent i = new Intent(getApplicationContext(), ProminentDisclosureActivity.class);
        startActivityForResult(i, PROMINENT_DISCLOSURE_ACTIVITY_REQUEST);
    }

    private void handleProminentDisclosureResult(int resultCode) {
        if (resultCode != RESULT_OK){
            Users.logout();
            loggedIn=false;
        }
    }

    /**
     * Event add dialog positive click listener
     *
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText eventNameText = (EditText) dialog.getDialog().findViewById(R.id.eventname);
        if ((eventNameText != null) && (eventNameText.getText() != null) && (!eventNameText.getText().toString().isEmpty())) {
            if (!eventNameText.getText().toString().matches(".*[\\.\\#\\$\\[\\]]+.*")) {
                Event e = addEvent(eventNameText.getText().toString(), ((EventInputDialog) dialog).yearStart,
                        ((EventInputDialog) dialog).yearEnd, ((EventInputDialog) dialog).monthStart,
                        ((EventInputDialog) dialog).monthEnd, ((EventInputDialog) dialog).dayStart,
                        ((EventInputDialog) dialog).dayEnd);
                if (e != null) {
                    AccessCodeShowDialog.showAccessCode(this, e);
                }
            }
        } else {
            Log.d(TAG, "Event not(!) created.");
            Toast t = Toast.makeText(this, R.string.new_event_name_not_accepted_toast_message, Toast.LENGTH_LONG);
            t.show();
        }
    }

    private Event addEvent(String eventNameText, int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
        if (users.getCurrentUser() != null) {
            if (commManager.getEvent(eventNameText) != null) {
                Log.d(TAG, "Event not(!) created. Event name exists in DB");
                Toast t = Toast.makeText(this, R.string.new_event_duplicate_name_toast_message, Toast.LENGTH_LONG);
                t.show();
                return null;
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
            e.accessCode = Event.generateAccessCode();
            commManager.writeEvent(e);
            Calendar start = Calendar.getInstance();
            start.set(yearStart, monthStart, dayStart);
            Calendar end = Calendar.getInstance();
            end.set(yearEnd, monthEnd, dayEnd);
            analytics.LogAddEvent(e.getName(), start.getTime(), end.getTime(), users.getCurrentUser());
            return e;
        } else {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("User not logged in tried to add new activity");
            crashlytics.log("E/TAG: User not logged in tried to add new activity");
            return null;
        }
    }

    public void viewOnly(Event event) {
        eventPressed(true, event);
    }
}

