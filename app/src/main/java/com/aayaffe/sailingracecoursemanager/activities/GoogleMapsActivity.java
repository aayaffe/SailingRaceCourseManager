package com.aayaffe.sailingracecoursemanager.activities;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.aayaffe.sailingracecoursemanager.dialogs.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.general.ConfigChange;
import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.general.Notification;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GPSService;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.crash.FirebaseCrash;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GoogleMapsActivity extends /*FragmentActivity*/AppCompatActivity implements BuoyInputDialog.BuoyInputDialogListener, LocationListener {
    private Notification notification = new Notification();
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
    GPSService mService;
    boolean mBound = false;
    private boolean viewOnly = false;
    private Legs legs;
    private RaceCourseDescriptor rcd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"OnCreate");
        setContentView(R.layout.activity_google_maps);
        noGps = findViewById(R.id.gps_indicator);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(unc);
        commManager = new FirebaseDB(this);
        commManager.login();
        Users.Init(commManager,sharedPreferences);
        users = Users.getInstance();
        mapLayer = new GoogleMaps();
        mapLayer.Init(this, this, sharedPreferences,getClickMethods());
        iGeo = new OwnLocation(getBaseContext(), this,this);
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        Intent i = getIntent();
        currentEventName = i.getStringExtra("eventName");
        viewOnly = i.getBooleanExtra("viewOnly",false);
        setIconsClickListeners();
        setupToolbar();
        Log.d(TAG, "Selected Event name is: " + currentEventName);
        FirebaseCrash.log("Current event name = " + currentEventName);
        commManager.subscribeToEventDeletion(commManager.getCurrentEvent(),true);
        ((FirebaseDB)commManager).setEventDeleted(new FirebaseDB.EventDeleted() {
            @Override
            public void onEventDeleted(Event e) {
                commManager.subscribeToEventDeletion(commManager.getCurrentEvent(),false);
                Log.i(TAG,"Closing activity due to event deletion");
                finish();
            }
        });
        Intent intent = new Intent(this, GPSService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        notification.InitNotification(this);
        setReturnedToEvent();

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
            menu.getItem(3).setEnabled(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(2).setEnabled(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).setVisible(false);
        } else {
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
            case R.id.action_exit:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
            if (users.getCurrentUser()== null) Log.e(TAG,"Current user is null");
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
//                myBoat.setLoc(iGeo.getLoc());
//                myBoat.lastUpdate = new Date().getTime();
//                myBoat.setLeftEvent(null);
//                commManager.writeBoatObject(myBoat);
            }
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
            if (GeoUtils.ageInSeconds(b.getLastUpdate())>900){
                boatsToRemove.add(b.getUUID());
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
        if (uid==null || o == null)
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
        if (requestCode == NEW_RACE_COURSE_REQUEST && resultCode == RESULT_OK) {
            RaceCourse rc = (RaceCourse) data.getExtras().getSerializable("RACE_COURSE");
            legs = (Legs) data.getExtras().getSerializable("LEGS");
            rcd = (RaceCourseDescriptor) data.getExtras().getSerializable("RCD");
            addRaceCourse(rc);
            firstBoatLoad = true;
        }

    }
    public void AddBuoyMenuItemOnClick() {
        Log.d(TAG, "Plus Fab Clicked");
        df = BuoyInputDialog.newInstance(-1, BuoyType.getBuoyTypes() ,this);
        df.show(getFragmentManager(), "Add_Buoy");
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
        //handler.removeCallbacks(runnable);

        Log.v(TAG, "onStop");
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.update(Integer.parseInt(sharedPreferences.getString("refreshRate", "5")) * 1000,myBoat,commManager.getCurrentEvent(),commManager,iGeo, users.getCurrentUser().Uid);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService.stop();
            mBound = false;
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
        handler.removeCallbacks(runnable);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mService.stop();
            mBound = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null && myBoat!=null){
            myBoat.setLoc(location);
            drawOwnBoat(myBoat);
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
            notification.cancelAll();
            commManager.writeLeaveEvent(users.getCurrentUser(),commManager.getCurrentEvent());
            // Unbind from the service
            if (mBound) {
                unbindService(mConnection);
                mService.stop();
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
}
