package com.aayaffe.sailingracecoursemanager.map;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.AnalyticsApplication;
import com.aayaffe.sailingracecoursemanager.AppPreferences;
import com.aayaffe.sailingracecoursemanager.Boats.BoatTypes;
import com.aayaffe.sailingracecoursemanager.Dialogs.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourse;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GoogleMapsActivity extends /*FragmentActivity*/AppCompatActivity implements BuoyInputDialog.BuoyInputDialogListener{

    private static final String TAG = "GoogleMapsActivity";
    public static  int REFRESH_RATE = 1000;
    private SharedPreferences SP;
    private GoogleMaps mapLayer;
    private ConfigChange unc = new ConfigChange();
    private IGeo iGeo;
    private Handler handler = new Handler();
    private WindArrow wa;
    public static ICommManager commManager;
    public static Marks marks = new Marks();
    private DialogFragment df;
    private static Users users;
    private static String currentEventName;
    private Tracker mTracker;
    private AviObject myBoat;
    private RaceCourse rc;
    private HashMap<String, BoatTypes> boatTypes;
    private boolean firstBoatLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);

        commManager = new Firebase(this);
        commManager.login(null, null,null/*SP.getString("username", "Manager1"), "Aa123456z", "1"*/);
        users = new Users(commManager);
        mapLayer = new GoogleMaps();
        mapLayer.Init(this, this, SP);
        iGeo  = new OwnLocation(getBaseContext());
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        Intent i = getIntent();
        //currentEvent =  i.getParcelableExtra("currentEvent");
        currentEventName = i.getStringExtra("eventName");
        ImageView ownLocationButton = (ImageView) findViewById(R.id.own_location);
        ownLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mapLayer.setCenter(iGeo.getLoc());
                }catch (Exception e)
                {
                    Log.d(TAG,"Unable to zoom to own location",e);
                }
            }
        });
        ImageView zoomToBoundsButton = (ImageView) findViewById(R.id.zoom_to_bounds);
        zoomToBoundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mapLayer.ZoomToMarks();
                }catch (Exception e)
                {
                    Log.d(TAG,"Unable to zoom to uuidToMarker",e);
                }
            }
        });
        ImageView noGPSIcon = (ImageView) findViewById(R.id.gps_indicator);
        noGPSIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Toast.makeText(GoogleMapsActivity.this, "No GPS signal Available",
                            Toast.LENGTH_LONG).show();
                }catch (Exception e)
                {
                    Log.d(TAG,"Error showing no GPS message",e);
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(currentEventName);

        Log.d(TAG, "Selected Event name is: " + currentEventName);
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        //mTracker = application.getDefaultTracker();
        boatTypes = ((Firebase)commManager).getAllBoatTypes();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_toolbar, menu);
        if((users.getCurrentUser()==null)||(!isCurrentEventManager(users.getCurrentUser().Uid))) {
            menu.getItem(2).setEnabled(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(1).setEnabled(false);
            menu.getItem(1).setVisible(false);
        }
        else
        {
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
                AddRaceCourse();
                firstBoatLoad = true;
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void AddRaceCourse() {
        mapLayer.removeAllMarks(); //TODO: Test
        if (rc==null){
            rc = new RaceCourse();
        }
        if (rc.getMarks()!=null){ //TODO : Check why being removed twice!
            for(AviObject ao:rc.getMarks().marks){
                mapLayer.removeMark(ao.getUUID());
            }
        }
        if ((commManager!=null)&&(commManager.getAllBuoys()!=null)) {
            for (AviObject ao : commManager.getAllBuoys()) {
                if (ao.getRaceCourseUUID() != null) {
                    mapLayer.removeMark(ao.getUUID());
                }
            }
        }
        String boatClass = SP.getString("boatClass", "470 Men");
        rc.setBoatVMGUpwind(getVMGUpwind(boatClass,(int) Float.parseFloat(SP.getString("windSpd", "14"))));
        rc.setBoatVMGRun(getVMGRun(boatClass, (int) Float.parseFloat(SP.getString("windSpd", "14"))));
        rc.setBoatVMGReach(getVMGReach(boatClass, (int) Float.parseFloat(SP.getString("windSpd", "14"))));
        rc.setSignalBoatLoc(GeoUtils.toAviLocation(iGeo.getLoc()));
        rc.setWindDir(Integer.parseInt(SP.getString("windDir", "0"))); //TODO putout to special function to get wind dir
        rc.setWindSpeed((int) Float.parseFloat(SP.getString("windSpd", "14")));
        rc.setBoatLength(getBoatLength(boatClass));
        rc.setGoalTime(Integer.parseInt(SP.getString("goalTime", "50")));
        rc.setRaceCourseType(rc.getRaceCourseType(SP.getString("rcType","Windward-leeward"))); //TODO redundant
        rc.setNumOfBoats(Integer.parseInt(SP.getString("numOfBoats", "25")));
        rc.calculateCourse(rc.getRaceCourseType(SP.getString("rcType","Windward-leeward")));
        Marks marks = rc.getMarks();
        removeAllRaceCourseMarks();//TODO : Check why being removed twice!
        for (AviObject m: marks.marks) {
            addMark(m);
        }
    }

    private void removeAllRaceCourseMarks() {
        marks.marks = commManager.getAllBuoys();
        for(AviObject m:marks.marks){
            if (m.getRaceCourseUUID()!=null){
                mapLayer.removeMark(m.getUUID());
            }
        }
    }


    private double getVMGUpwind(String boatClass, int windSpeed){
        if (windSpeed<=8) return boatTypes.get(boatClass).getUpwind5_8();
        if (windSpeed<=12) return boatTypes.get(boatClass).getUpwind8_12();
        if (windSpeed<=15) return boatTypes.get(boatClass).getUpwind12_15();
        else return boatTypes.get(boatClass).getUpwind15_();
    }
    private double getVMGRun(String boatClass, int windSpeed){
        if (windSpeed<=8) return boatTypes.get(boatClass).getRun5_8();
        if (windSpeed<=12) return boatTypes.get(boatClass).getRun8_12();
        if (windSpeed<=15) return boatTypes.get(boatClass).getRun12_15();
        else return boatTypes.get(boatClass).getRun15_();
    }
    private double getVMGReach(String boatClass, int windSpeed){
        if (windSpeed<=8) return boatTypes.get(boatClass).getReach5_8();
        if (windSpeed<=12) return boatTypes.get(boatClass).getReach8_12();
        if (windSpeed<=15) return boatTypes.get(boatClass).getReach12_15();
        else return boatTypes.get(boatClass).getReach15_();
    }
    private double getBoatLength(String boatClass){
        return boatTypes.get(boatClass).getLength();

    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if ((users.getCurrentUser()!=null)&&(commManager.getAllBoats()!=null)) {
                if (myBoat == null) {
                    for(AviObject ao: commManager.getAllBoats()){
                        if (ao.name == users.getCurrentUser().DisplayName)
                        {
                            myBoat=ao;
                            break;
                        }
                    }
                    if(myBoat==null){
                        myBoat = new AviObject();
                    }
                    myBoat.name = users.getCurrentUser().DisplayName;
                    myBoat.setLoc(iGeo.getLoc());
                    myBoat.color = "Blue"; //TODO Set properly
                    myBoat.lastUpdate = new Date();
                    if (isCurrentEventManager(users.getCurrentUser().Uid)) {
                        myBoat.setEnumType(ObjectTypes.RaceManager);
                    } else myBoat.setEnumType(ObjectTypes.WorkerBoat);
                } else {
                    myBoat.setLoc(iGeo.getLoc());
                    myBoat.lastUpdate = new Date();
                }
                commManager.writeBoatObject(myBoat);
            }
            if (((OwnLocation)iGeo).isGPSFix()){
                findViewById(R.id.gps_indicator).setVisibility(View.INVISIBLE);
            }
            else
            {
                findViewById(R.id.gps_indicator).setVisibility(View.VISIBLE);
            }
            redrawLayers();
            handler.postDelayed(runnable, (Integer.parseInt(SP.getString("refreshRate", "1")) * 1000));
        }
    };

    private boolean isCurrentEventManager(String Uid){
        if (commManager.getEvent(currentEventName)==null)
            return false;
        if (Uid == null||Uid.isEmpty())
            return false;
        return commManager.getEvent(currentEventName).getEventManager().Uid.equals(Uid);
    }

    public static boolean isCurrentEventManager(){
        if (commManager.getEvent(currentEventName)==null)
            return false;
        if (users.getCurrentUser() == null|| users.getCurrentUser().Uid==null||users.getCurrentUser().Uid.isEmpty())
            return false;
        return commManager.getEvent(currentEventName).getEventManager().Uid.equals(users.getCurrentUser().Uid);
    }

    public void redrawLayers() {
        Location myLocation = iGeo.getLoc();
        marks.marks = commManager.getAllBoats();
        for (AviObject o: marks.marks) {
            //TODO: Handle in case of user is logged out or when database does not contain current user.
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(!o.name.equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(o, getDirDistTXT(myLocation,o.getLoc()), id);
            }
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(o.name.equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(o, null, id);
            }
        }
        List<AviObject> markList = commManager.getAllBuoys();
        for (AviObject o : markList){
            //TODO: Delete old buoys first
            if(o.getEnumType() ==ObjectTypes.FlagBuoy) {
                switch(o.color){
                    case "Red":
                        mapLayer.addMark(o,getDirDistTXT(myLocation, o.getLoc()),R.mipmap.flag_buoy_red);
                        break;
                    case "Blue":
                        mapLayer.addMark(o,getDirDistTXT(myLocation, o.getLoc()),R.mipmap.flag_buoy_blue);
                        break;
                    case "Yellow":
                        mapLayer.addMark(o,getDirDistTXT(myLocation, o.getLoc()),R.mipmap.flag_buoy_yellow);
                        break;
                    case "Orange":
                    default:
                        mapLayer.addMark(o,getDirDistTXT(myLocation, o.getLoc()),R.mipmap.flag_buoy_orange);
                        break;
                }
            }
            else if(o.getEnumType() ==ObjectTypes.TomatoBuoy) {
                switch(o.color) {
                    case "Red":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.tomato_buoy_red);
                        break;
                    case "Blue":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.tomato_buoy_blue);
                        break;
                    case "Yellow":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.tomato_buoy_yellow);
                        break;
                    case "Orange":
                    default:
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.tomato_buoy_orange);
                        break;
                }
            }
            else if(o.getEnumType() ==ObjectTypes.TriangleBuoy) {
                switch(o.color) {
                    case "Red":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.triangle_buoy_red);
                        break;
                    case "Blue":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.triangle_buoy_blue);
                        break;
                    case "Yellow":
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.triangle_buoy_yellow);
                        break;
                    case "Orange":
                    default:
                        mapLayer.addMark(o, getDirDistTXT(myLocation, o.getLoc()), R.mipmap.triangle_buoy_orange);
                        break;
                }
            }
            else
                mapLayer.addMark(o,getDirDistTXT(myLocation, o.getLoc()),R.drawable.buoyblack);

        }
        if ((marks.marks.size()>0)&&(firstBoatLoad)&&(mapLayer.mapView!=null))
        {
            firstBoatLoad = false;
            mapLayer.ZoomToMarks();
        }
    }

    private int getIconId(String string, AviObject o) {
        int ret = R.drawable.boatred;
        if (o.name.equals(string)){
            switch(o.getEnumType()) {
                case WorkerBoat:
                    ret = R.drawable.boatgold;
                    if (AviLocation.Age(o.getAviLocation())>300)
                        ret = R.drawable.boatred;
                    break;
                case RaceManager: ret = R.drawable.managergold;
                    break;
                default: ret = R.drawable.boatred;
            }
        }
        else {
            switch (o.getEnumType()) {
                case WorkerBoat:
                    ret = R.drawable.boatcyan;
                    if (AviLocation.Age(o.getAviLocation())>300)
                        ret = R.drawable.boatred;
                    break;
                case RaceManager:
                    ret = R.drawable.managerblue;
                    break;
                default:
                    ret = R.drawable.boatred;
            }
        }
        return ret;
    }

    private String getDirDistTXT(Location src, Location dst){
        int distance;
        int bearing;
        String units;
        try {
            distance = src.distanceTo(dst) < 5000 ? (int) src.distanceTo(dst) : ((int) (src.distanceTo(dst) / 1609.34));
            units = src.distanceTo(dst)<5000?"m":"NM";
            bearing = src.bearingTo(dst) > 0 ? (int) src.bearingTo(dst) : (int) src.bearingTo(dst)+360;
        }catch (NullPointerException e)
        {
            return "NoGPS";
        }
        return bearing + "\\" + distance + units;
    }
    @Override
    protected void onStart() {
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
        Intent i = new Intent(getApplicationContext(), AppPreferences.class);
        if (isCurrentEventManager(users.getCurrentUser().Uid)){
            i.putExtra("MANAGER", true);
        }
        else{
            i.putExtra("MANAGER", false);
        }

        startActivity(i);
    }

    public void AddMenuItemOnClick() {
        Log.d(TAG, "Plus Fab Clicked");
        df = BuoyInputDialog.newInstance(-1,this);
        df.show(getFragmentManager(), "Add_Buoy");
    }


    private void addMark(long id, Location loc, Float dir, int dist){
        if (loc == null) return;
        AviObject o =new AviObject();
        o.setEnumType(ObjectTypes.Buoy);//// TODO: 11/02/2016 Add bouy types
        o.color = "Black";
        o.lastUpdate = new Date(System.currentTimeMillis());
        o.setLoc(GeoUtils.getLocationFromDirDist(loc,dir,dist));
        o.name = "Buoy"+id;
        o.id = id;
        addMark(o);
    }

    private void addMark(AviObject m){
        commManager.writeBuoyObject(m);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText dirText = (EditText) dialog.getDialog().findViewById(R.id.dir);
        EditText distText = (EditText) dialog.getDialog().findViewById(R.id.dist);
        long buoyId = ((BuoyInputDialog)df).buoy_id;
        if (buoyId!=-1){
            addMark(buoyId,iGeo.getLoc(), Float.parseFloat(dirText.getText().toString()), Integer.parseInt(distText.getText().toString()));
        }
        else
            addMark(newBuoyId(),iGeo.getLoc(), Float.parseFloat(dirText.getText().toString()), Integer.parseInt(distText.getText().toString()));
    }

    private long newBuoyId() {
        return commManager.getNewBuoyId();
    }

    public static void login(String id){
        if (commManager !=null) {
            commManager.login(id, "Aa123456z", "1");
            Log.d(TAG, "login to " + id);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        Log.d(TAG,"OnStop");
//        finish();
    }

}
