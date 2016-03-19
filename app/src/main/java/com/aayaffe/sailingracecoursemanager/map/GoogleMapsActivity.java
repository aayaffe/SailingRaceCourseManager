package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.AppPreferences;
import com.aayaffe.sailingracecoursemanager.BuoyEditDialog;
import com.aayaffe.sailingracecoursemanager.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.Firebase;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.general.Notification;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;

import java.util.Date;
import java.util.List;

public class GoogleMapsActivity extends FragmentActivity implements BuoyInputDialog.BuoyInputDialogListener, BuoyEditDialog.BuoyEditDialogListener{

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
    private Users users;
    private String currentEventName;



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

        Log.d(TAG,"Selected Event name is: " + currentEventName);
    }

    public void PopUpMenu(Context c, Activity a)
    {
        PopupMenu popupMenu = new PopupMenu(c, findViewById(R.id.addFAB));
        popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener)a);
        popupMenu.inflate(R.menu.map_popup_menu);
        popupMenu.show();
    }


    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            AviObject o = new AviObject();
            if (users.getCurrentUser()!=null) {
                o.name = users.getCurrentUser().DisplayName;
                o.setLoc(iGeo.getLoc());
                o.color = "Blue"; //TODO Set properly
                if (isCurrentEventManager(users.getCurrentUser().Uid)) {
                    o.type = ObjectTypes.RaceManager;
                } else o.type = ObjectTypes.WorkerBoat;
                //TODO Think about last update time (Exists in Location also)
                commManager.writeBoatObject(o);
            }
            redrawLayers();
            handler.postDelayed(runnable, (Integer.parseInt(SP.getString("refreshRate", "10")) * 1000));
        }
    };

    private boolean isCurrentEventManager(String Uid){
        if (commManager.getEvent(currentEventName)==null)
            return false;
        if (Uid == null||Uid.isEmpty())
            return false;
        if (commManager.getEvent(currentEventName).getEventManager().Uid.equals(Uid))
            return true;
        return false;
    }

    public void redrawLayers() {
        Location myLocation = iGeo.getLoc();
        marks.marks = commManager.getAllBoats();
        for (AviObject o: marks.marks) {
            //TODO: Handle in case of user is logged out or when database does not contain current user.
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(!o.name.equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(GeoUtils.toLatLng(o.getLoc()),o.getLoc().getBearing(), o.name, getDirDistTXT(myLocation,o.getLoc()), id);
            }
            if ((o != null)&&(o.getLoc()!=null)&&(users.getCurrentUser()!=null)&&(o.name.equals(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/))) {
                int id = getIconId(users.getCurrentUser().DisplayName/*SP.getString("username","Manager1")*/,o);
                mapLayer.addMark(GeoUtils.toLatLng(o.getLoc()),o.getLoc().getBearing(), o.name, null, id);
            }
        }
        List<AviObject> markList = commManager.getAllBuoys();
        for (AviObject o : markList){
            //TODO: Delete old buoys first
            mapLayer.addMark(GeoUtils.toLatLng(o.getLoc()),o.getLoc().getBearing(),o.name,getDirDistTXT(myLocation, o.getLoc()),R.drawable.buoyblack);

        }
    }

    private int getIconId(String string, AviObject o) {
        int ret = R.drawable.boatred;
        if (o.name.equals(string)){
            switch(o.type) {
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
            switch (o.type) {
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
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));
        Float rotation = Float.parseFloat(SP.getString("windDir", "90"));
        Log.d(TAG, "New wind arrow rotation is " + rotation);
        wa.setDirection(rotation);
        Log.d(TAG, "New wind arrow icon rotation is " + wa.getDirection());

        runnable.run();
    }
    public void fabOnClick(View v) {
        Log.d(TAG, "FAB Setting Clicked");
        Intent i = new Intent(getApplicationContext(), AppPreferences.class);
        startActivity(i);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        map.destroy();
//    }
    public void plusIconOnclick(View view) {
        Log.d(TAG, "Plus Fab Clicked");
        df = BuoyInputDialog.newInstance(-1);
        df.show(getFragmentManager(), "Add_Buoy");
    }


    private void addMark(long id, Location loc, Float dir, int dist){
        if (loc == null) return;
        AviObject o =new AviObject();
        o.type = ObjectTypes.Buoy;//// TODO: 11/02/2016 Add bouy types
        o.color = "Black";
        o.lastUpdate = new Date(System.currentTimeMillis());
        o.setLoc(GeoUtils.getLocationFromDirDist(loc,dir,dist));
        o.name = "Buoy"+id;
        o.id = id;
        commManager.writeBuoyObject(o);
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

    public static void resetMap(){
        
    }


    public void onMoveButtonClick() {
        long id = ((BuoyEditDialog)mapLayer.df).buoy_id;
        //dialog.dismiss();
        df = BuoyInputDialog.newInstance(id);
        df.show(getFragmentManager(), "Edit_Buoy");
    }
    public void onDeleteButtonClick() {
        long id = ((BuoyEditDialog)mapLayer.df).buoy_id;
        //dialog.dismiss();
        mapLayer.removeMark(id);
    }
    @Override
    public void onEditDialogPositiveClick(DialogFragment dialog) {

    }



}
