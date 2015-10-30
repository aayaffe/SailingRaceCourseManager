package com.aayaffe.sailingracecoursemanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.communication.CommStub;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.communication.Object;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.communication.QuickBlox;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.general.Notification;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.map.MapUtils;
import com.aayaffe.sailingracecoursemanager.map.OpenSeaMap;
import com.aayaffe.sailingracecoursemanager.map.SamplesBaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.util.MapViewerTemplate;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OnlineTileSource;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends SamplesBaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    // name of the map file in the external storage
    private static OpenSeaMap map = new OpenSeaMap();
    private static ICommManager qb;
    private Handler handler = new Handler();
    private Marker myLoc = new Marker(null,null,0,0);
    private static Map<String,Marker> workerLocs = new HashMap<String, Marker>();
    private static Map<String,TextMarker> workerTexts = new HashMap<String, TextMarker>();
    private Polyline rbLine = new Polyline(null, AndroidGraphicFactory.INSTANCE);
    private int ID = 1;
    private static String TAG = "MainActivity";
    private ConfigChange unc = new ConfigChange();
    private ImageView windArrow;
    public static int REFRESH_RATE = 10000;
    private Notification notification = new Notification();
    GoogleApiClient mGoogleApiClient;
    SharedPreferences SP;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    protected TileDownloadLayer downloadLayer;


    EditText mEdit;
    private Runnable runnable = new Runnable()
    {

        public void run()
        {
            qb.sendLoc(mLastLocation);
            redrawLayers();
            handler.postDelayed(runnable, REFRESH_RATE);
        }
    };


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected int getMapViewId() {
        return R.id.mapView;
    }


    @Override
    protected String getMapFileName() {
        return "germany.map";
    }


    @Override
    protected XmlRenderTheme getRenderTheme() {
        return null;
    }

    @Override
    protected void createLayers() {
        this.downloadLayer = new TileDownloadLayer(this.tileCaches.get(0),
                this.mapView.getModel().mapViewPosition, OpenStreetMapMapnik.INSTANCE,
                AndroidGraphicFactory.INSTANCE);
        mapView.getLayerManager().getLayers().add(this.downloadLayer);

        mapView.getModel().mapViewPosition.setZoomLevelMin(OpenStreetMapMapnik.INSTANCE.getZoomLevelMin());
        mapView.getModel().mapViewPosition.setZoomLevelMax(OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());
        mapView.getMapZoomControls().setZoomLevelMin(OpenStreetMapMapnik.INSTANCE.getZoomLevelMin());
        mapView.getMapZoomControls().setZoomLevelMax(OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());
    }


    @Override
    protected void createMapViews() {
        super.createMapViews();
        // we need to set a fixed size tile as the raster tiles come at a fixed size and not being blurry
        this.mapView.getModel().displayModel.setFixedTileSize(256);
    }
    public void fabOnClick(View v) {
        Log.d(TAG,"FAB Setting Clicked");
        Intent i = new Intent(getApplicationContext(), AppPreferences.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        windArrow = (ImageView) findViewById(R.id.windArrow);

        buildGoogleApiClient();
        map.MapInit(this, getMapView());
        qb = new QuickBlox(this,getResources());
        //qb = new CommStub();
        qb.login(SP.getString("username", "Manager1"), "Aa123456z", "1");
        runnable.run();
        if ((myLoc!=null)&&(myLoc.getLatLong()!=null)){
            map.setCenter(myLoc.getLatLong());
        }
        else {
            map.setCenter(32.9, 34.9);//TODO: Find better solution
        }
        map.setZoomLevel(8);
        notification.InitNotification(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        windArrow = (ImageView) findViewById(R.id.windArrow);
        Float rotation = Float.parseFloat(SP.getString("windDir", "90"));
        Log.d(TAG, "New wind arrow rotation is " + rotation);
        windArrow.setRotation(rotation + 90);
        Log.d(TAG, "New wind arrow icon rotation is "+windArrow.getRotation());
        redrawLayers();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.destroy();
    }
     public static void login(String id){
        if (qb!=null) {
            qb.login(id, "Aa123456z", "1");
            Log.d(TAG,"login to " + id);
        }
    }
    public void redrawLayers()
    {
        super.redrawLayers();
        GraphicFactory gf = AndroidGraphicFactory.INSTANCE;
        Location myLocation = mLastLocation;

        List<Object> l = qb.getAllLocs();
        for (Object o: l) {
            if ((o != null)&&(!o.name.equals(SP.getString("username","Manager1")))) {
                Marker m;
                if (workerLocs.containsKey(o.name))
                {
                    m = workerLocs.get(o.name);
                    m.setLatLong(GeoUtils.toLatLong(o.location));
                }
                else{
                    Bitmap b = AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatred));
                    if(o.color.contains("blue")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatblue));
                    if(o.color.contains("cyan")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatcyan));
                    if(o.color.contains("orange")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatorange));
                    if(o.color.contains("green")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatgreen));
                    if(o.color.contains("pink")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatpink));
                    if (o.type==ObjectTypes.RaceManager){
                        b =AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.managerblue));
                    }

                    if((o.lastUpdate!=null)&&(GeneralUtils.dateDifference(o.lastUpdate)>2000))
                    {
                        b = AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatred));
                    }
                    b = MapUtils.addBoatNumber(b,Character.getNumericValue(o.name.charAt(o.name.length() - 1)),getResources());

                    m = new Marker(GeoUtils.toLatLong(o.location),b,0,0);
                }
                workerLocs.put(o.name, m);
                try {

                    map.removeMark(m);
                    map.addMark(m);
                }catch (IllegalStateException e)
                {
                    Log.e(TAG,"Error adding layers",e);
                }

                if (myLocation!=null){
                    Paint p = gf.createPaint();
                    p.setColor(Color.BLACK);
                    p.setTextSize((int) (16 * getResources().getDisplayMetrics().density));
                    int distance = myLocation.distanceTo(o.location)<5000?(int)myLocation.distanceTo(o.location):((int)(myLocation.distanceTo(o.location)/1609.34));
                    Log.v(TAG, "Distance to user " + o.name + " " + myLocation.distanceTo(o.location));
                    String units = myLocation.distanceTo(o.location)<5000?"m":"NM";
                    int bearing = myLocation.bearingTo(o.location) > 0 ? (int) myLocation.bearingTo(o.location) : (int) myLocation.bearingTo(o.location)+360;
                    TextMarker tm;
                    if (workerTexts.containsKey(o.name)){
                        tm = workerTexts.get(o.name);
                        tm.setText(bearing + "\\" + distance + units);
                        tm.setLatLong(GeoUtils.toLatLong(o.location));
                    }
                    else{
                        tm = new TextMarker(bearing + "\\" + distance + units,p,GeoUtils.toLatLong(o.location),AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatblue)));
                    }
                    workerTexts.put(o.name, tm);
                    try {
//
                        map.removeMark(tm);
                        map.addMark(tm);
                    }catch (IllegalStateException e) {
                        Log.e(TAG, "Error adding layers", e);
                    }
                }
            }
            if (myLocation!=null) {
                if (SP.getString("username","Manager1").contains("Manager")){
                    myLoc.setBitmap(AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.managergold)));
                }else
                    myLoc.setBitmap(AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatgold)));
                myLoc.setLatLong(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
                try {
                    if (!map.contains(myLoc)) {

                        map.addMark(myLoc);
                    }
                }catch (IllegalStateException e) {
                    Log.e(TAG, "Error adding layers", e);
                }
            }
        }

////            rbLine.setPaintStroke(paintStroke);
////            List<LatLong> geoPoints = rbLine.getLatLongs();
////            geoPoints.clear();
////            geoPoints.add(new LatLong(l.getLatitude(), l.getLongitude()));
////            geoPoints.add(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
////            this.mapView.getLayerManager().getLayers().remove(rbLine);
////            this.mapView.getLayerManager().getLayers().add(rbLine);
//            //zoomToBounds(new LatLong(l.getLatitude(), l.getLongitude()),new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));

    }





    public static void resetMap(){


        for(Marker m: workerLocs.values()){
            map.removeMark(m);
        }
        for(TextMarker tm:workerTexts.values()){
            map.removeMark(tm);
        }

        workerLocs.clear();
        workerTexts.clear();
        //map.destroy();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            //mBuilder.setOngoing(false);
            notification.cancelAll();
            finish(); // finish activity
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
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

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        //if (mRequestingLocationUpdates) {
            startLocationUpdates();
        //}
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
        {LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);}

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        resetMap();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()/* && !mRequestingLocationUpdates*/) {
            startLocationUpdates();
        }
        redrawLayers();
    }
}


