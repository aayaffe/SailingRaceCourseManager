package com.aayaffe.sailingracecoursemanager.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Map_Layer.GoogleMaps;
import com.aayaffe.sailingracecoursemanager.Map_Layer.MapClickMethods;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.calclayer.BuoyType;
import com.aayaffe.sailingracecoursemanager.calclayer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.dialogs.AccessCodeShowDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.general.ConfigChange;
import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GPSService;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.android.gms.location.LocationListener;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GoogleMapsActivity extends /*FragmentActivity*/AppCompatActivity implements BuoyInputDialog.BuoyInputDialogListener, LocationListener {
//    private Notification notification = new Notification();
    private Boolean exit = false;
    public static List<DBObject> buoys;
    public static List<DBObject> boats;
    private DBObject myBoat; //instead of AviObject class

    private static final String TAG = "GoogleMapsActivity";
    public static int refreshRate = 1000;
    private static Users users;
    private SharedPreferences sharedPreferences;
    private GoogleMaps mapLayer;
    private ConfigChange unc = new ConfigChange();
    private IGeo iGeo;
    private Handler handler = new Handler();
    private WindArrow wa;
    public static IDBManager getCommManager() {
        return commManager;
    }
    private static IDBManager commManager;
    private DialogFragment df;
    private static String currentEventName;
    private boolean firstBoatLoad = true;
    private DBObject assignedTo = null;
    public static final int NEW_RACE_COURSE_REQUEST = 770;
    private ImageView noGps;
    private DBObject assignedBuoy;
    private GPSService mService;
    boolean mBound = false;
    private boolean viewOnly = false;
    private Legs legs;
    private RaceCourseDescriptor rcd;
    // The BroadcastReceiver used to listen from broadcasts from the @link GPSService.
    private LocationReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        initClassVars();
        Intent i = getIntent();
        currentEventName = i.getStringExtra("eventName");
        viewOnly = i.getBooleanExtra("viewOnly",false);
        setIconsClickListeners();
        setupToolbar();
        Log.d(TAG, "Selected Event name is: " + currentEventName);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.log("Current event name = " + currentEventName);
        commManager.subscribeToEventDeletion(commManager.getCurrentEvent(),true);
        ((FirebaseDB)commManager).setEventDeleted(e -> {
            commManager.subscribeToEventDeletion(commManager.getCurrentEvent(),false);
            Log.i(TAG,"Closing activity due to event deletion");
            finish();
        });
        if (!viewOnly) {
            Intent intent = new Intent(this, GPSService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
//        notification.InitNotification(this);
        setReturnedToEvent();
    }

    private void initClassVars() {
        noGps = findViewById(R.id.gps_indicator);
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(unc);
        commManager = FirebaseDB.getInstance(this);
        Users.Init(commManager,sharedPreferences);
        users = Users.getInstance();
        mapLayer = new GoogleMaps();
        mapLayer.Init(this, this, sharedPreferences,getClickMethods());
        iGeo = new OwnLocation(getBaseContext(), this,this);
        myReceiver = new LocationReceiver();
    }

    private void setReturnedToEvent() {
        if(users.getCurrentUser()!=null) {
            myBoat = commManager.getBoatByUserUid(users.getCurrentUser().Uid);
            if (myBoat!=null) {
                myBoat.setLeftEvent(null);
                commManager.writeBoatObject(myBoat);
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            toolbar.setTitle(currentEventName);
        }
        else
            Log.e(TAG,"Unable to find toolbar view.");


    }

    @Contract(" -> !null")
    private MapClickMethods getClickMethods() {
        return new MapClickMethods() {
            @Override
            public void infoWindowClick(UUID u) {
                DBObject b = commManager.getObjectByUUID(u);
                if (b==null)
                    return;
                if (b.equals(assignedTo))
                { //Turn off assignment
                    assignBuoyUIUpdate((DBObject) null);
                }
                else if (isBuoy(b)){
                    assignBuoyUIUpdate(u);
                }
            }
            @Override
            public void infoWindowLongClick(UUID u) {
                Log.d(TAG, "OninfowindowLongClick: "+ u.toString());
                boolean isBuoy = isBuoy(commManager.getObjectByUUID(u));
                if (isBuoy && GoogleMapsActivity.isCurrentEventManager() && !viewOnly) {
                    mapLayer.removeMark(u,true);
                }
            }
        };
    }

    private void assignBuoyUIUpdate(UUID u) {
        assignBuoyUIUpdate(commManager.getObjectByUUID(u));
    }
    private void assignBuoyUIUpdate(DBObject b){
        if (myBoat==null){
            return;
        }
        if (b==null){
            TextView tv = (TextView) findViewById(R.id.goto_text_view);
            if(tv!=null) {
                tv.setVisibility(View.INVISIBLE);
            }
            assignedTo = null;
            mapLayer.addLine(null,null);
            return;
        }
        mapLayer.addLine(b.getUUID(),myBoat.getUUID());
        TextView tv = (TextView) findViewById(R.id.goto_text_view);
        tv.setVisibility(View.VISIBLE);
        tv.setText(b.getName()+'\n'+getDirDistTXT(myBoat.getLoc(), b.getLoc()));
        assignedTo = b;
        firstBoatLoad = true;
    }

    @Contract("null -> false")
    private boolean isBuoy(DBObject b) {
        if (b==null){
            return false;
        }
        switch (b.getBuoyType()){
            case BUOY:
            case FINISH_LINE:
            case FLAG_BUOY:
            case GATE:
            case START_FINISH_LINE:
            case START_LINE:
            case TOMATO_BUOY:
            case TRIANGLE_BUOY:
                return true;
            case RACE_OFFICER:
            case MARK_LAYER:
            case REFERENCE_POINT:
            case OTHER:
            default:
                return false;
        }
    }

    private void setIconsClickListeners() {
        setOnClickListenerToView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapLayer.setCenter(iGeo.getLoc());
                } catch (Exception e) {
                    Log.d(TAG, "Unable to zoom to own location", e);
                }
            }
        }, R.id.own_location);
        setOnClickListenerToView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapLayer.ZoomToMarks();
                } catch (Exception e) {
                    Log.d(TAG, "Unable to zoom to uuidToMarker", e);
                }
            }
        }, R.id.zoom_to_bounds);
        setOnClickListenerToView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(GoogleMapsActivity.this, "No GPS signal Available",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d(TAG, "Error showing no GPS message", e);
                }
            }
        },R.id.gps_indicator);
    }

    /**
     * Sets on click listeners to views.
     * Logs an error when view is not found.
     * @param onClickListener
     * @param id
     */
    private void setOnClickListenerToView(View.OnClickListener onClickListener, int id) {
        View v = findViewById(id);
        if(v!=null)
            v.setOnClickListener(onClickListener);
        else Log.e(TAG,"Unable to set onClickListener on view id: " + id);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_toolbar, menu);
        if ((users.getCurrentUser() == null) || (!isCurrentEventManager(users.getCurrentUser().Uid)) || viewOnly) {
            menu.getItem(4).setEnabled(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(3).setEnabled(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(2).setEnabled(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(4).setEnabled(true);
            menu.getItem(4).setVisible(true);
            menu.getItem(3).setEnabled(true);
            menu.getItem(3).setVisible(true);
            menu.getItem(2).setEnabled(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(1).setVisible(true);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsMenuItemOnClick();
                return true;
            case R.id.action_add_object:
                AddBuoyMenuItemOnClick();
                return true;
            case R.id.action_add_race_course:
                addRaceCourseItemClick();
                firstBoatLoad = true;
                return true;
            case R.id.action_assign_buoys:
                openAssignBuoyActvity();
                return true;
            case R.id.action_show_access_code:
                AccessCodeShowDialog.showAccessCode(this,commManager.getCurrentEvent());
                return true;
            case R.id.action_exit:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void AddBuoyMenuItemOnClick() {
        Log.d(TAG, "add buoy menu item clicked");
        df = BuoyInputDialog.newInstance(-1, BuoyType.getBuoyTypes() ,this);
        df.show(getFragmentManager(), "Add_Buoy");
    }
    private void addRaceCourseItemClick() {
        Intent i = new Intent(getApplicationContext(), MainCourseInputActivity.class);
        i.putExtra("LEGS",legs);
        i.putExtra("RCD",rcd);
        startActivityForResult(i,NEW_RACE_COURSE_REQUEST);
//        Intent i = new Intent(getApplicationContext(), AddRaceCourseActivity.class);
//        startActivityForResult(i,NEW_RACE_COURSE_REQUEST);
    }

    private void openAssignBuoyActvity() {
        Intent intent = new Intent(getApplicationContext(), ChooseBoatActivity.class);
        startActivity(intent);
    }

    private void addRaceCourse(RaceCourse rc) {
        removeAllRaceCourseMarks();
        if (mapLayer.mapView != null) {
            buoys.addAll(rc.getBuoyList());
        }
        else Log.w(TAG, "null map");
        addBuoys();
        drawMapComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setReturnedToEvent();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(GPSService.ACTION_BROADCAST));
        Log.v(TAG, "onResume");
    }

    private void removeAllRaceCourseMarks() {
        List<DBObject> buoysToRemove = new ArrayList<>();
        for (DBObject buoy : buoys) {
            if (buoy.getRaceCourseUUID() != null) {

                if (assignedTo!=null && assignedTo.equals(buoy))
                {
                    assignBuoyUIUpdate((DBObject)null);
                }
                buoysToRemove.add(buoy);
            }
        }
        for (DBObject buoy:buoysToRemove){
            mapLayer.removeMark(buoy.getUUID(),true);
            buoys.remove(buoy);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG,"in run()");
            if (users.getCurrentUser()== null) Log.e(TAG,"Current user is null");

            if (((OwnLocation) iGeo).isGPSFix()) {
                noGps.setVisibility(View.INVISIBLE);
            } else {
                noGps.setVisibility(View.VISIBLE);
            }

            List<DBObject> assignedBuoys = commManager.getAssignedBuoys(myBoat);
            if (assignedBuoys==null||assignedBuoys.isEmpty())
                assignedBuoy = null;
            else
                assignedBuoy = assignedBuoys.get(0);
            drawMapComponents();
            handler.postDelayed(runnable, Integer.parseInt(sharedPreferences.getString("refreshRate", "5")) * 1000);
            assignBuoyUIUpdate(assignedBuoy);

        }
    };

    private boolean isCurrentEventManager(String uid) {
        Event e = commManager.getCurrentEvent();
        return e != null && !(uid == null || uid.isEmpty()) && e.getManagerUuid() != null && e.getManagerUuid().equals(uid);
    }

    public static boolean isCurrentEventManager() {
        Event e = commManager.getCurrentEvent();
        return e != null && !(users.getCurrentUser() == null || users.getCurrentUser().Uid == null || users.getCurrentUser().Uid.isEmpty() || e.getManagerUuid() == null) && e.getManagerUuid().equals(users.getCurrentUser().Uid);
    }

    public void addBuoys() {
        for (DBObject buoy : buoys) {
            mapLayer.addBuoy(buoy, getDirDistTXT(iGeo.getLoc(), buoy.getLoc()));
            commManager.writeBuoyObject(buoy);
        }
    }

    public void drawMapComponents() {
        boats = commManager.getAllBoats();
        buoys = commManager.getAllBuoys();
        if (isCurrentEventManager() && !viewOnly){
            removeOldBoats(boats);
        }
        removeOldMarkers(boats, buoys);
        for (DBObject boat : boats) {
            if((boat==null)||(boat.getLoc() == null) || (users.getCurrentUser() == null)) continue;
            if ((!isOwnObject(users.getCurrentUser().Uid, boat))) {
                mapLayer.addMark(boat, getDirDistTXT(iGeo.getLoc(), boat.getLoc()), getIconId(users.getCurrentUser().Uid, boat), getZIndex(boat));
            }
            if ((isOwnObject(users.getCurrentUser().Uid, boat))) {
                drawOwnBoat(boat);
            }
        }
        for (DBObject buoy : buoys) {
            mapLayer.addBuoy(buoy, getDirDistTXT(iGeo.getLoc(), buoy.getLoc()));
        }
        if ((!buoys.isEmpty()) && (firstBoatLoad) && (mapLayer.mapView != null)) {
            firstBoatLoad = false;
            mapLayer.ZoomToMarks();
        } else if ((firstBoatLoad) && (mapLayer.mapView != null)){
            firstBoatLoad = false;
            if (myBoat!=null) {
                mapLayer.setZoom(10, myBoat.getLoc());
            }
        }
    }

    private synchronized void drawOwnBoat(DBObject boat) {
        if (users.getCurrentUser() == null)
            return;
        //int id = getIconId(users.getCurrentUser().DisplayName, boat);
        int id = getIconId(users.getCurrentUser().Uid, boat);
        mapLayer.addMark(boat, null, id,getZIndex(boat));
        assignBuoyUIUpdate(assignedBuoy);
    }
    private int getZIndex(DBObject boat) {
        if (isOwnObject(users.getCurrentUser().Uid,boat))
            return 10;
        return 0;
    }
    private void removeOldBoats(List<DBObject> boats){
        List<UUID> boatsToRemove = new LinkedList<>();
        for(DBObject b:boats){
            //900 == 15 minutes
            if (b.getAviLocation()!=null) {
                if (GeoUtils.ageInSeconds(b.getAviLocation().lastUpdate) > 900) {
                    boatsToRemove.add(b.getUUID());
                }
            }else{
                if (GeoUtils.ageInSeconds(b.getLastUpdate()) > 900) {
                    boatsToRemove.add(b.getUUID());
                }
            }
        }
        for(UUID u: boatsToRemove){
            commManager.removeBoat(u);
        }
    }
    private void removeOldMarkers(List<DBObject> boats, List<DBObject> buoys) {
        List<UUID> uuids = new LinkedList<>();
        for (DBObject b : boats) {
            uuids.add(b.getUUID());
        }
        for (DBObject b : buoys) {
            uuids.add(b.getUUID());
        }
        List<UUID> markerToRemove = new LinkedList<>();
        for (UUID u: mapLayer.uuidToMarker.keySet())
        {
            if (!uuids.contains(u)){
                markerToRemove.add(u);
            }
        }
        for (UUID u:markerToRemove) {

            if (assignedTo!=null && assignedTo.getUUID().equals(u))
            {
                assignBuoyUIUpdate((DBObject)null);
            }
            mapLayer.removeMark(u,false);
        }
    }
    private int getIconId(String uid, DBObject o) {
        int ret;
        if (uid==null || o == null || o.getBuoyType()==null)
            return R.drawable.boatred;
        if (isOwnObject(uid, o)) {
            switch (o.getBuoyType()) {
                case MARK_LAYER:
                    ret = R.drawable.boatgold;
                    if (AviLocation.Age(o.getAviLocation()) > 60 || o.getLeftEventAsDate()!=null)
                        ret = R.drawable.boatred;
                    break;
                case RACE_OFFICER:
                    ret = R.drawable.managergold;
                    break;
                default:
                    ret = R.drawable.boatred;
            }
        } else {
            switch (o.getBuoyType()) {
                case MARK_LAYER:
                    ret = R.drawable.boatcyan;
                    if (AviLocation.Age(o.getAviLocation()) > 60 || o.getLeftEventAsDate()!=null)
                        ret = R.drawable.boatred;
                    break;
                case RACE_OFFICER:
                    ret = R.drawable.managerblue;
                    break;
                default:
                    ret = R.drawable.boatred;
            }
        }
        return ret;
    }
    private boolean isOwnObject(String uid, DBObject o) {
        return o != null && o.userUid != null && o.userUid.equals(uid);
    }
    @Contract("null, _ -> !null")
    private String getDirDistTXT(Location src, Location dst) {
        if (src==null){
            return "NoGPS";
        }
        int maxMetreDistance = 500;
        double distance;
        int bearing;
        String units;
        try {
            distance = src.distanceTo(dst) < maxMetreDistance ? src.distanceTo(dst) : (src.distanceTo(dst) / GeoUtils.NM2m);
            units = src.distanceTo(dst) < maxMetreDistance ? getString(R.string.metre_unit_symbol) : getString(R.string.nautical_miles_unit_symbol);
            bearing = src.bearingTo(dst) > 0 ? (int) src.bearingTo(dst) : (int) src.bearingTo(dst) + 360;
        } catch (NullPointerException e) {
            Log.e(TAG,"No gps found", e);
            return getString(R.string.no_gps);
        }
        if (bearing==360)
            bearing = 0;
        if (units.equals(getString(R.string.nautical_miles_unit_symbol)))
            return String.format(Locale.getDefault(),"%03d", bearing) + "\\" + String.format(Locale.getDefault(),"%1$.2f", distance) + units;
        return String.format(Locale.getDefault(),"%03d", bearing) + "\\" + String.format(Locale.getDefault(),"%1$.0f", distance) + units;
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "OnStart");
        super.onStart();
        firstBoatLoad = true;
        updateWindArrow();
        Log.d(TAG, "New wind arrow icon rotation is " + wa.getDirection());
        commManager = FirebaseDB.getInstance(this);
        runnable.run();
    }

    private void updateWindArrow() {
        wa = new WindArrow((ImageView) findViewById(R.id.windArrow));
        Float rotation = Float.parseFloat(sharedPreferences.getString("windDir", "90"));
        Log.d(TAG, "New wind arrow rotation is " + rotation);
        wa.setDirection(rotation);
    }

    public void SettingsMenuItemOnClick() {
        Log.d(TAG, "FAB Setting Clicked");
        Intent i = new Intent(getApplicationContext(), MainCourseInputActivity.class);
        startActivityForResult(i,NEW_RACE_COURSE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_RACE_COURSE_REQUEST && resultCode == RESULT_OK) {
            RaceCourse rc = (RaceCourse) data.getExtras().getSerializable("RACE_COURSE");
            legs = (Legs) data.getExtras().getSerializable("LEGS");
            rcd = (RaceCourseDescriptor) data.getExtras().getSerializable("RCD");
            addRaceCourse(rc);
            firstBoatLoad = true;
        }

    }

    private void addMark(long id, Location loc, Float dir, Float dist, BuoyType buoyType) {
        if (loc == null)
            return;
        if (dir==null||dist==null)
            return;
        DBObject o = new DBObject("BUOY" + id, new AviLocation(GeoUtils.toAviLocation(loc), dir, dist), Color.BLACK, buoyType);
        o.id = id;
        addMark(o);
    }
    private void addMark(DBObject m) {
        commManager.writeBuoyObject(m);
    }
    /**
     * Add buoy dialog click
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Spinner s = (Spinner) dialog.getDialog().findViewById(R.id.select_buoy_type);
        EditText dirText = (EditText) dialog.getDialog().findViewById(R.id.dir);
        EditText distText = (EditText) dialog.getDialog().findViewById(R.id.dist);
        if (dirText==null||distText==null)
            return;
        if (dirText.getText()==null||distText.getText()==null)
            return;
        if (GeneralUtils.isValid(dirText.getText().toString(),Float.class,0f,360f)&&GeneralUtils.isValid(dirText.getText().toString(),Float.class,0f,null)) {
            long buoyId = ((BuoyInputDialog) df).buoyId;
            if (buoyId != -1) {
                addMark(buoyId, iGeo.getLoc(), Float.parseFloat(dirText.getText().toString()), Float.parseFloat(distText.getText().toString()), BuoyType.valueOf((String) s.getSelectedItem()));
            } else
                addMark(newBuoyId(), iGeo.getLoc(), GeneralUtils.tryParseFloat(dirText.getText().toString()), GeneralUtils.tryParseFloat(distText.getText().toString()), BuoyType.valueOf((String) s.getSelectedItem()));
        }
    }

    private long newBuoyId() {
        return commManager.getNewBuoyId();
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            if (users.getCurrentUser()!=null) {
                GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                int refreshRate = Integer.parseInt(sharedPreferences.getString("refreshRate", "5")) * 1000;
                Log.i(TAG,"Set refresh rate: "+ refreshRate);
                mService.update(refreshRate);
                mService.requestLocationUpdates();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService.removeLocationUpdates();
            mService = null;
            mBound = false;
        }
    };
    @Override
    protected void onDestroy() {
        Log.v(TAG,"onDestroy");
        handler.removeCallbacks(runnable);
        // Unbind from the service
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        iGeo.stopLocationUpdates();
        if (mBound) {
            mService.removeLocationUpdates();
            Log.d(TAG,"Removed location updates");
            unbindService(mConnection);
            mService = null;
            mBound = false;
        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if ((users.getCurrentUser() != null) && (commManager.getAllBoats() != null) && !viewOnly) {
            myBoat = commManager.getBoatByUserUid(users.getCurrentUser().Uid);
            if (myBoat == null) {
                myBoat = new DBObject(users.getCurrentUser().DisplayName, GeoUtils.toAviLocation(iGeo.getLoc()), Color.BLUE, BuoyType.MARK_LAYER);//TODO Set color properly
                if (isCurrentEventManager(users.getCurrentUser().Uid)) {
                    myBoat.setBuoyType(BuoyType.RACE_OFFICER);
                } else myBoat.setBuoyType(BuoyType.MARK_LAYER);
                myBoat.userUid = users.getCurrentUser().Uid;
                myBoat.setLeftEvent(null);
                commManager.writeBoatObject(myBoat);
            }
        }
        if(location!=null && myBoat!=null && !viewOnly){
            myBoat.setLoc(location);
            drawOwnBoat(myBoat);
            myBoat.lastUpdate = new Date().getTime();
            commManager.updateBoatLocation(commManager.getCurrentEvent(), myBoat, myBoat.getAviLocation());
        }
        if (((OwnLocation) iGeo).isGPSFix()) {
            noGps.setVisibility(View.INVISIBLE);
        } else {
            noGps.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onBackPressed() {
        if (exit) {
//            notification.cancelAll();
            commManager.writeLeaveEvent(users.getCurrentUser(),commManager.getCurrentEvent());
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
            // Unbind from the service
            if (mBound) {
                mService.removeLocationUpdates();
                unbindService(mConnection);
                mService.stop();
                mService = null;
                mBound = false;
            }
            finish();
        } else {
            Toast.makeText(this, "Press back again to leave event.",
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

    /**
     * Receiver for broadcasts sent by {@link GPSService}.
     */
    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG,"onReceive location");
            Location location = intent.getParcelableExtra(GPSService.EXTRA_LOCATION);
            if (location != null) {
                onLocationChanged(location);
            }
        }
    }
}
