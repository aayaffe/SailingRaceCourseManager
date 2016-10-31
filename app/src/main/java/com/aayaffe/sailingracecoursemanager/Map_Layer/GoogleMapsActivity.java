package com.aayaffe.sailingracecoursemanager.Map_Layer;

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
import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.BuoyType;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.Dialogs.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GoogleMapsActivity extends /*FragmentActivity*/AppCompatActivity implements BuoyInputDialog.BuoyInputDialogListener{

    private RaceCourse raceCourse;  //imported from jonathan's new RaceCourse class //replaces private RaceCourse rc;
    public static List<Buoy> buoys; //replaces public static marks marks = new marks();
    //private HashMap<String, BoatTypes> boatTypes;  //unnecessary, now is a part ot the xml boat parser
    private Buoy myBoat; //instead of AviObject class

    private static final String TAG = "GoogleMapsActivity";
    public static  int REFRESH_RATE = 1000;
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
    private Tracker mTracker;
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
        iGeo  = new OwnLocation(getBaseContext(), this);
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

        //boatTypes = ((Firebase)commManager).getAllBoatTypes();


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
            case R.id.action_exit:
                System.exit(0);
                finish();
                System.exit(0);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void AddRaceCourse() {
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
        mapLayer.removeAllMarks();
        raceCourse = new RaceCourse(this,  myBoat.getAviLocation() , Integer.parseInt(SP.getString("windDir", "0")), Float.parseFloat(SP.getString("Dist2m1", "0.5")) , Integer.parseInt(SP.getString("StartLineLength", "0.111")),(HashMap<String,String>)SP.getStringSet("CourseOptions", null));  //defultStartLine: 200m

         buoys.addAll(raceCourse.convertMarks2Bouys());

        /*   //i don't get why is this part exist...
        if (raceCourse==null){
            raceCourse = new RaceCourse();
        }
        if (raceCourse.getMarks()!=null){
            for(Bouy buoy2remove: raceCourse.getBouyList()){
                mapLayer.removeMark(buoy2remove.getUuid());
            }
        }
        if ((commManager!=null)&&(commManager.getAllBuoys()!=null)) {
            for (Buoy buoy2remove : commManager.getAllBuoys()) {
                if (buoy2remove.getRaceCourseUUID() != null) {
                    mapLayer.removeMark(buoy2remove.getUuid());
                }
            }
        }

        raceCourse = new RaceCourse(context,  signalBoatLoc, windDirection, distance2mark1 , startLineDistance, selectedCourseOptions );
        removeAllRaceCourseMarks();
        for (Buoy m: buoys) {
            addMark(m);
        }*/
    }

    private void removeAllRaceCourseMarks() {  //becomes unnecessary
        buoys = commManager.getAllBuoys();
        for(Buoy m: buoys){
            if (m.getRaceCourseUUID()!=null){
                mapLayer.removeMark(m.getUUID());
            }
        }
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            if ((users.getCurrentUser()!=null)&&(commManager.getAllBoats()!=null)) {
                if (myBoat == null) {
                    for(Buoy ao: commManager.getAllBoats()){
                        if (ao.getName() == users.getCurrentUser().DisplayName)
                        {
                            myBoat=ao;
                            break;
                        }
                    }
                    if(myBoat==null){
                        myBoat = new Buoy(users.getCurrentUser().DisplayName, GeoUtils.toAviLocation(iGeo.getLoc()), "Blue");//TODO Set color properly
                    }
                    if (isCurrentEventManager(users.getCurrentUser().Uid)) {
                        myBoat.setBuoyType(BuoyType.RaceManager);
                    } else myBoat.setBuoyType(BuoyType.WorkerBoat);
                } else {
                    myBoat.setLoc(iGeo.getLoc());
                    myBoat.lastUpdate = new Date().getTime();
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
        buoys = commManager.getAllBoats();
        for (Buoy o: buoys) {
            //TODO: Handle in case of user is logged out or when database does not contain current user.
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(!o.getName().equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(o, getDirDistTXT(myLocation,o.getLoc()), id);
            }
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(o.getName().equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(o, null, id);
            }
        }
        List<Buoy> commBuoyList = commManager.getAllBuoys();
        for (Buoy o : commBuoyList){
            //TODO: Delete old buoys first
            if(o.getBuoyType() ==BuoyType.FlagBuoy) {
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
            else if(o.getBuoyType() ==BuoyType.TomatoBuoy) {
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
            else if(o.getBuoyType() ==BuoyType.TriangleBuoy) {
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
        if ((buoys.size()>0)&&(firstBoatLoad)&&(mapLayer.mapView!=null))
        {
            firstBoatLoad = false;
            mapLayer.ZoomToMarks();
        }
    }

    private int getIconId(String string, Buoy o) {
        int ret = R.drawable.boatred;
        if (o.getName().equals(string)){
            switch(o.getBuoyType()) {
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
            switch (o.getBuoyType()) {
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
        Buoy o =new Buoy("Buoy"+id, new AviLocation(GeoUtils.toAviLocation(loc),Integer.parseInt(dir+""),dist), "Black", BuoyType.Buoy);// TODO: 11/02/2016 Add bouy types
        o.id = id;
        addMark(o);
    }

    private void addMark(Buoy m){
        commManager.writeBuoyObject(m);
    }

    public List<Buoy> getBuoys() {
        return buoys;
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
