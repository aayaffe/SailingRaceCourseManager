package com.aayaffe.sailingracecoursemanager.Map_Layer;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.BuoyType;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.Dialogs.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Input_UI_Layer.MainCourseInputActivity;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.aayaffe.sailingracecoursemanager.manage.ChooseBoatActivity;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GoogleMapsActivity extends /*FragmentActivity*/AppCompatActivity implements BuoyInputDialog.BuoyInputDialogListener {

    public static List<Buoy> buoys; //replaces public static marks marks = new marks();
    public static List<Buoy> boats;
    private Buoy myBoat; //instead of AviObject class

    private static final String TAG = "GoogleMapsActivity";
    public static int REFRESH_RATE = 1000;
    private static Users users;
    private SharedPreferences SP;
    private GoogleMaps mapLayer;
    private ConfigChange unc = new ConfigChange();
    private IGeo iGeo;
    private Handler handler = new Handler();
    private WindArrow wa;
    public static ICommManager commManager;
    private DialogFragment df;
    private static String currentEventName;
    private boolean firstBoatLoad = true;
    private Buoy assignedTo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);

        commManager = new Firebase(this);
        commManager.login(/*null, null,null*/SP.getString("username", "Manager1"), "Aa123456z", "1");
        users = new Users(commManager);
        mapLayer = new GoogleMaps();
        mapLayer.Init(this, this, SP,getClickMethods());
        iGeo = new OwnLocation(getBaseContext(), this);
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        Intent i = getIntent();
        currentEventName = i.getStringExtra("eventName");
        SetIconsClickListeners();
        SetupToolbar();

        Log.d(TAG, "Selected Event name is: " + currentEventName);
    }

    private void SetupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null)
            toolbar.setTitle("");
        else Log.e(TAG,"Unable to find toolbar view.");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(currentEventName);
    }

    private MapClickMethods getClickMethods() {
        MapClickMethods ret = new MapClickMethods() {
            @Override
            public void infoWindowClick(UUID u) {
                Buoy b = commManager.getObjectByUUID(u);
                if (b==null) return;
                //TODO: Implement something here...
            }

            @Override
            public void infoWindowLongClick(UUID u) {
                Buoy b = commManager.getObjectByUUID(u);
                if (b==null) return;
                if (b.equals(assignedTo))
                {
                    assignBuoy((Buoy) null);
                }
                else if (isBuoy(b)){
                    assignBuoy(u);
                }


            }
        };
        return ret;
    }

    private void assignBuoy(UUID u) {
        assignBuoy(commManager.getObjectByUUID(u));
    }
    private void assignBuoy(Buoy b){
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
    }

    private boolean isBuoy(Buoy b) {
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
        }
        return false;
    }

    private void SetIconsClickListeners() {
        SetOnClickListenerToView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapLayer.setCenter(iGeo.getLoc());
                } catch (Exception e) {
                    Log.d(TAG, "Unable to zoom to own location", e);
                }
            }
        }, R.id.own_location);
        SetOnClickListenerToView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapLayer.ZoomToMarks();
                } catch (Exception e) {
                    Log.d(TAG, "Unable to zoom to uuidToMarker", e);
                }
            }
        }, R.id.zoom_to_bounds);
        SetOnClickListenerToView(new View.OnClickListener() {
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
    private void SetOnClickListenerToView(View.OnClickListener onClickListener, int id) {
        View v = findViewById(id);
        if(v!=null)
            v.setOnClickListener(onClickListener);
        else Log.e(TAG,"Unable to set onClickListener on view id: " + id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_toolbar, menu);
        if ((users.getCurrentUser() == null) || (!isCurrentEventManager(users.getCurrentUser().Uid))) {
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
                AddMenuItemOnClick();
                return true;

            case R.id.action_add_race_course:
                addRaceCourse();
                firstBoatLoad = true;
                return true;
            case R.id.action_assign_buoys:
                OpenAssignBuoyActvity();
                return true;
            case R.id.action_exit:
                //System.exit(0);
                finish();
                //System.exit(0);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void OpenAssignBuoyActvity() {
        Intent intent = new Intent(getApplicationContext(), ChooseBoatActivity.class);
        startActivity(intent);
    }

    private void addRaceCourse() {
        /**
         * args:
         * distance to mark 1
         * wind direction
         * signal boat location
         * course options
         * context
         *
         * all GoogleMapsActivity variables
         */
        removeAllRaceCourseMarks();
//        if (mapLayer.mapView != null) mapLayer.removeAllMarks();


        RaceCourse raceCourse = new RaceCourse();
        if (mapLayer.mapView != null) buoys.addAll(raceCourse.getBuoyList());
        else Log.w(TAG, "null map");

        addBuoys();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addRaceCourse();
        Log.w(TAG, "onResume");
    }

    private void removeAllRaceCourseMarks() {  //becomes unnecessary
        buoys = commManager.getAllBuoys();
        for (Buoy m : buoys) {
            if (m.getRaceCourseUUID() != null) {
                mapLayer.removeMark(m.getUUID());
            }
        }
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if ((users.getCurrentUser() != null) && (commManager.getAllBoats() != null)) {
                myBoat = getMyBoat(users.getCurrentUser().DisplayName);
                if (myBoat == null) {
                    myBoat = new Buoy(users.getCurrentUser().DisplayName, GeoUtils.toAviLocation(iGeo.getLoc()), Color.BLUE, BuoyType.WORKER_BOAT);//TODO Set color properly
                }
                if (isCurrentEventManager(users.getCurrentUser().Uid)) {
                    myBoat.setBuoyType(BuoyType.RACE_MANAGER);
                } else myBoat.setBuoyType(BuoyType.WORKER_BOAT);
                myBoat.setLoc(iGeo.getLoc());
                myBoat.lastUpdate = new Date().getTime();
                commManager.writeBoatObject(myBoat);
            }
            if (((OwnLocation) iGeo).isGPSFix()) {
                findViewById(R.id.gps_indicator).setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.gps_indicator).setVisibility(View.VISIBLE);
            }
            drawMapComponents();
            handler.postDelayed(runnable, (Integer.parseInt(SP.getString("refreshRate", "5")) * 1000));
            assignBuoy(assignedTo);

        }
    };
    private Buoy getMyBoat(String name){
        for (Buoy ao : commManager.getAllBoats()) {
            if (ao.getName().equals(name)) {
                return ao;
            }
        }
        return null;
    }


    private boolean isCurrentEventManager(String Uid) {
        Event e = commManager.getEvent(currentEventName);
        if (e == null)
            return false;
        if (Uid == null || Uid.isEmpty())
            return false;
        Log.d(TAG, "Checking for event " + currentEventName + " manager: " + commManager.getEvent(currentEventName).getManagerUuid());
        return e.getManagerUuid() != null && e.getManagerUuid().equals(Uid);
    }

    public static boolean isCurrentEventManager() {
        Event e = commManager.getEvent(currentEventName);
        if (e == null)
            return false;
        return !(users.getCurrentUser() == null || users.getCurrentUser().Uid == null || users.getCurrentUser().Uid.isEmpty()) && e.getManagerUuid().equals(users.getCurrentUser().Uid);
    }

    public void addBuoys() {
        for (Buoy buoy : buoys) {
            mapLayer.addBuoy(buoy, getDirDistTXT(iGeo.getLoc(), buoy.getLoc()));
            commManager.writeBuoyObject(buoy);
        }
    }

    public void drawMapComponents() {
        Location myLocation = iGeo.getLoc();
        boats = commManager.getAllBoats();
        for (Buoy boat : boats) {
            //TODO: Handle in case of user is logged out or when database does not contain current user.
            if ((boat != null) && (boat.getLoc() != null) && (users.getCurrentUser() != null) && (!boat.getName().equals(users.getCurrentUser().DisplayName))) {
                Log.d(TAG, "drawMapComponents() first if is true");
                int id = getIconId(users.getCurrentUser().DisplayName, boat);
                mapLayer.addMark(boat, getDirDistTXT(myLocation, boat.getLoc()), id);
            }
            if ((boat != null) && (boat.getLoc() != null) && (users.getCurrentUser() != null) && (boat.getName().equals(users.getCurrentUser().DisplayName))) {
                Log.d(TAG, "drawMapComponents() second if is true");
                int id = getIconId(users.getCurrentUser().DisplayName, boat);
                mapLayer.addMark(boat, null, id);
            }
        }
        buoys = commManager.getAllBuoys();
        Log.d(TAG, "commBuoyList size: " + buoys.size());
        for (Buoy buoy : buoys) {
            mapLayer.addBuoy(buoy, getDirDistTXT(myLocation, buoy.getLoc()));
        }
        if ((buoys.size() > 0) && (firstBoatLoad) && (mapLayer.mapView != null)) {
            firstBoatLoad = false;
            mapLayer.ZoomToMarks();
        } else if ((firstBoatLoad) && (mapLayer.mapView != null)){
            mapLayer.setZoom(10,myBoat.getLoc());
        }
    }

    private int getIconId(String string, Buoy o) {
        int ret;
        if (o.getName().equals(string)) {
            switch (o.getBuoyType()) {
                case WORKER_BOAT:
                    ret = R.drawable.boatgold;
                    if (AviLocation.Age(o.getAviLocation()) > 300)
                        ret = R.drawable.boatred;
                    break;
                case RACE_MANAGER:
                    ret = R.drawable.managergold;
                    break;
                default:
                    ret = R.drawable.boatred;
            }
        } else {
            switch (o.getBuoyType()) {
                case WORKER_BOAT:
                    ret = R.drawable.boatcyan;
                    if (AviLocation.Age(o.getAviLocation()) > 300)
                        ret = R.drawable.boatred;
                    break;
                case RACE_MANAGER:
                    ret = R.drawable.managerblue;
                    break;
                default:
                    ret = R.drawable.boatred;
            }
        }
        return ret;
    }

    private String getDirDistTXT(Location src, Location dst) {
        int distance;
        int bearing;
        String units;
        try {
            distance = src.distanceTo(dst) < 500 ? (int) src.distanceTo(dst) : ((int) (src.distanceTo(dst) / 1609.34));
            units = src.distanceTo(dst) < 500 ? "m" : "NM";
            bearing = src.bearingTo(dst) > 0 ? (int) src.bearingTo(dst) : (int) src.bearingTo(dst) + 360;
        } catch (NullPointerException e) {
            return "NoGPS";
        }
        if (bearing==360) bearing = 0;
        return String.format("%03d", bearing) + "\\" + distance + units;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "OnStart");
        super.onStart();
        firstBoatLoad = true;
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        Float rotation = Float.parseFloat(SP.getString("windDir", "90"));
        Log.d(TAG, "New wind arrow rotation is " + rotation);
        wa.setDirection(rotation);
        Log.d(TAG, "New wind arrow icon rotation is " + wa.getDirection());
        runnable.run();
    }

    public void SettingsMenuItemOnClick() {
        Log.d(TAG, "FAB Setting Clicked");
        Intent i = new Intent(getApplicationContext(), MainCourseInputActivity.class);
        //if (isCurrentEventManager(users.getCurrentUser().Uid)){  //TODO:Test: delete all // annotations
        //    i.putExtra("MANAGER", true);
        /*}
        else{
            i.putExtra("MANAGER", false);
        }
        */
        startActivity(i);
    }

    public void AddMenuItemOnClick() {
        Log.d(TAG, "Plus Fab Clicked");
        df = BuoyInputDialog.newInstance(-1, this);
        df.show(getFragmentManager(), "Add_Buoy");
    }


    private void addMark(long id, Location loc, Float dir, int dist) {
        if (loc == null) return;
        Buoy o = new Buoy("BUOY" + id, new AviLocation(GeoUtils.toAviLocation(loc), Integer.parseInt(dir + ""), dist), Color.BLACK, BuoyType.BUOY);// TODO: 11/02/2016 Add bouy types
        o.id = id;
        addMark(o);
    }

    private void addMark(Buoy m) {
        commManager.writeBuoyObject(m);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText dirText = (EditText) dialog.getDialog().findViewById(R.id.dir);
        EditText distText = (EditText) dialog.getDialog().findViewById(R.id.dist);
        long buoyId = ((BuoyInputDialog) df).buoy_id;
        if (buoyId != -1) {
            addMark(buoyId, iGeo.getLoc(), Float.parseFloat(dirText.getText().toString()), Integer.parseInt(distText.getText().toString()));
        } else
            addMark(newBuoyId(), iGeo.getLoc(), Float.parseFloat(dirText.getText().toString()), Integer.parseInt(distText.getText().toString()));
    }

    private long newBuoyId() {
        return commManager.getNewBuoyId();
    }

    public static void login(String id) {
        if (commManager != null) {
            commManager.login(id, "Aa123456z", "1");
            Log.d(TAG, "login to " + id);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        Log.d(TAG, "OnStop");
//        finish();
    }


}
