package com.aayaffe.sailingracecoursemanager.map;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.MainActivity;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.TextMarker;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "GoogleMapsActivity";
    private SharedPreferences SP;
    private GoogleMap mMap;
    private GoogleMaps mapLayer = new GoogleMaps();
    private ConfigChange unc = new ConfigChange();
    private IGeo iGeo;
    private Handler handler = new Handler();
    private static Map<String,Marker> workerLocs = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);
        iGeo  = new OwnLocation(getBaseContext());

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
        LatLng sydney = new LatLng(-34, 151);
        mapLayer.setCenter(sydney);
        mapLayer.addMark(sydney, "SydneyMarker", R.drawable.buoyblack);
    }



    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            AviObject o = new AviObject();
            o.name = SP.getString("username", "Manager1");
            o.location = iGeo.getLoc();
            o.color = "Blue"; //TODO Set properly
            o.type = ObjectTypes.WorkerBoat;//TODO Set properly
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
        
                mapLayer.addMark(GeoUtils.toLatLng(o.location),o.name,MainActivity.marks.getIconID(o));
            }

        }
        List<AviObject> markList = MainActivity.commManager.getAllBuoys();
        for (AviObject o : markList){
            //TODO: Delete old buoys first
            mapLayer.addMark(GeoUtils.toLatLng(o.location),"Buoy",R.drawable.buoyblack);

        }

    }

}
