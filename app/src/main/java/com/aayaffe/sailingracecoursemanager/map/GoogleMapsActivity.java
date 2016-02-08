package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.AppPreferences;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.MainActivity;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.geographical.WindArrow;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "GoogleMapsActivity";
    private SharedPreferences SP;
    private GoogleMap mMap;
    private GoogleMaps mapLayer;
    private ConfigChange unc = new ConfigChange();
    private IGeo iGeo;
    private Handler handler = new Handler();
    private static Map<String,Marker> workerLocs = new HashMap<>();
    private WindArrow wa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapLayer = new GoogleMaps();
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);
        iGeo  = new OwnLocation(getBaseContext());
        wa = new WindArrow(((ImageView) findViewById(R.id.windArrow)));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location center = new Location("Manual");
        center.setLatitude(32.9);
        center.setLongitude(34.9);
        mapLayer.Init(this, this, mMap, SP, center, 1);
        runnable.run();
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        mapLayer.setCenter(GeoUtils.toLatLng(iGeo.getLoc()));
        //mapLayer.addMark(sydney, "SydneyMarker", R.drawable.buoyblack);
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
            o.name = SP.getString("username", "Manager1");
            o.location = iGeo.getLoc();
            o.color = "Blue"; //TODO Set properly
            o.type = ObjectTypes.RaceManager;//TODO Set properly
            //TODO Think about last update time (Exists in Location also)
            MainActivity.commManager.writeBoatObject(o);
            redrawLayers();
            Log.d(TAG, "Delaying runnable for " + (Integer.parseInt(SP.getString("refreshRate", "10")) * 1000) + " ms");
            handler.postDelayed(runnable, (Integer.parseInt(SP.getString("refreshRate", "10")) * 1000));
        }
    };

    public void redrawLayers()
    {
        Location myLocation = GeoUtils.toLocation(iGeo.getLoc());
        MainActivity.marks.marks = MainActivity.commManager.getAllBoats();
        for (AviObject o: MainActivity.marks.marks) {
            if ((o != null)&&(!o.name.equals(SP.getString("username","Manager1")))) {
                mapLayer.addMark(GeoUtils.toLatLng(o.location), o.name, getDirDistTXT(myLocation, GeoUtils.toLocation(o.location)), MainActivity.marks.getIconID(o));
            }
            if ((o != null)&&(o.name.equals(SP.getString("username","Manager1")))) {
                mapLayer.addMark(GeoUtils.toLatLng(o.location),o.name,null,MainActivity.marks.getIconID(o));
            }

        }
        List<AviObject> markList = MainActivity.commManager.getAllBuoys();
        for (AviObject o : markList){
            //TODO: Delete old buoys first
            mapLayer.addMark(GeoUtils.toLatLng(o.location),"Buoy",getDirDistTXT(myLocation, GeoUtils.toLocation(o.location)),R.drawable.buoyblack);

        }
    }

    private String getDirDistTXT(Location src, Location dst){
        int distance;
        try {
            distance = src.distanceTo(dst) < 5000 ? (int) src.distanceTo(dst) : ((int) (src.distanceTo(dst) / 1609.34));
        }catch (NullPointerException e)
        {
            distance = -1;
        }
        String units = src.distanceTo(dst)<5000?"m":"NM";
        int bearing = src.bearingTo(dst) > 0 ? (int) src.bearingTo(dst) : (int) src.bearingTo(dst)+360;
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
        //redrawLayers();
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

}
