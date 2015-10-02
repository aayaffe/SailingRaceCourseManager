package com.aayaffe.sailingracecoursemanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.communication.*;
import com.aayaffe.sailingracecoursemanager.communication.Object;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.MockLocation;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.map.MapUtils;

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
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    // name of the map file in the external storage
    private static final String MAPFILE = "andorra.map";
    private static MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    static private IGeo gps ;
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
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;

    SharedPreferences SP;

    EditText mEdit;
    private Runnable runnable = new Runnable()
    {

        public void run()
        {
            qb.sendLoc(gps.getLoc());
            redrawLayers();
            handler.postDelayed(runnable, REFRESH_RATE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(unc);
        //Floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        windArrow = (ImageView) findViewById(R.id.windArrow);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AppPreferences.class);
                startActivity(i);
            }
        });

        ////////////////////////////////




        AndroidGraphicFactory.createInstance(this.getApplication());
        this.mapView = new MapView(this);
        RelativeLayout rlMap = (RelativeLayout) findViewById(R.id.rlMap);
        rlMap.addView(mapView, 0);
        //setContentView(this.mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(false);
        this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        gps = new OwnLocation(this);
        qb = new QuickBlox(this,getResources());
        //qb.login("SRCMWorker"+ID, "Aa123456z", "1");
        qb.login(SP.getString("username", "Manager1"), "Aa123456z", "1");
        runnable.run();
        if ((myLoc!=null)&&(myLoc.getLatLong()!=null)){
            this.mapView.getModel().mapViewPosition.setCenter(myLoc.getLatLong());
        }
        else {
            this.mapView.getModel().mapViewPosition.setCenter(new LatLong(32.9, 34.9));
        }
        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 8);
        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification_icon).setContentTitle("AVI is running!").setContentText("The app is sending and receiving data.");
        Intent resultIntent = new Intent(this,MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        // Sets an ID for the notification
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    @Override
    protected void onStart() {
        super.onStart();

        windArrow.setRotation(Float.parseFloat(SP.getString("windDir","90"))+90);
        // tile renderer layer using internal render theme
//        MapDataStore mapDataStore = new MapFile(getMapFile());
//        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
//                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
//        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        // only once a layer is associated with a mapView the rendering starts
        //this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        //qb.sendLoc(null);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mapView.destroyAll();
    }

    private File getMapFile() {
        File file = new File(Environment.getExternalStorageDirectory(), MAPFILE);
        return file;
    }




    public Location getLoc() {
        return gps.getLoc();
    }


     public static void login(String id){
        if (qb!=null) {
            qb.login(id, "Aa123456z", "1");
            Log.d(TAG,"login to " + id);

//            if (id.contains("Worker4")) {
//                gps = new MockLocation();
//                Log.d(TAG,"Switched to MockLocation");
//            }
        }
    }
    public void redrawLayers()
    {
        GraphicFactory gf = AndroidGraphicFactory.INSTANCE;

//        Paint paintStroke = gf.createPaint();
//        paintStroke.setStyle(Style.STROKE);
//        paintStroke.setStrokeWidth(7);
//        paintStroke.setColor(Color.BLUE);
//        paintStroke.setDashPathEffect(new float[] { 25, 15 });
//        paintStroke.setStrokeWidth(5);
//        paintStroke.setStrokeWidth(3);
        Location myLocation = gps.getLoc();
        if (myLocation!=null) {
            if (SP.getString("username","Manager1").contains("Manager")){
                myLoc.setBitmap(AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.managergold)));
            }else
                myLoc.setBitmap(AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatgold)));
            myLoc.setLatLong(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
            this.mapView.getLayerManager().getLayers().remove(myLoc);
            this.mapView.getLayerManager().getLayers().add(myLoc);
            //this.mapView.getModel().mapViewPosition.setCenter(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));

        }
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
                this.mapView.getLayerManager().getLayers().remove(m);
                this.mapView.getLayerManager().getLayers().add(m);
                if (myLocation!=null){
                    Paint p = gf.createPaint();
                    p.setColor(Color.BLACK);
                    p.setTextSize(50);
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
                        tm = new TextMarker(bearing + "\\" + distance + units,p,GeoUtils.toLatLong(o.location),AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.worker)));
                    }
                    workerTexts.put(o.name, tm);
                    this.mapView.getLayerManager().getLayers().remove(tm);
                    this.mapView.getLayerManager().getLayers().add(tm);
                }
            }
        }
//        if((l!=null)&&(myLocation!=null)){
//
//
//
////            rbLine.setPaintStroke(paintStroke);
////            List<LatLong> geoPoints = rbLine.getLatLongs();
////            geoPoints.clear();
////            geoPoints.add(new LatLong(l.getLatitude(), l.getLongitude()));
////            geoPoints.add(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
////            this.mapView.getLayerManager().getLayers().remove(rbLine);
////            this.mapView.getLayerManager().getLayers().add(rbLine);
//            Paint p = gf.createPaint();
//            p.setColor(Color.BLACK);
//            p.setTextSize(50);
//            int distance = myLocation.distanceTo(l)>10?(int)myLocation.distanceTo(l):((int)(myLocation.distanceTo(l)/1609.34));
//            String units = myLocation.distanceTo(l)>10?"NM":"Yd";
//            int bearing = myLocation.bearingTo(l) > 0 ? (int) myLocation.bearingTo(l) : (int) myLocation.bearingTo(l)+360;
//            workerText.setText(bearing + "\\" + distance + units);
//            workerText.setPaing(p);
//            workerText.setLatLong(new LatLong(l.getLatitude(), l.getLongitude()));
//            workerText.setBitmap(AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.worker)));
//            this.mapView.getLayerManager().getLayers().remove(workerText);
//            this.mapView.getLayerManager().getLayers().add(workerText);
//            //zoomToBounds(new LatLong(l.getLatitude(), l.getLongitude()),new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
//        }
    }

    private void zoomToBounds(LatLong ll1, LatLong ll2)
    {
        BoundingBox bb = getBoundingBox(ll1,ll2);
        Dimension dimension = this.mapView.getModel().mapViewDimension.getDimension();
        this.mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
                bb.getCenterPoint(),
                LatLongUtils.zoomForBounds(dimension,bb,this.mapView.getModel().displayModel.getTileSize())));
    }


    private BoundingBox getBoundingBox(LatLong ll1, LatLong ll2){
        double minLat,maxLat,minLon,maxLon;
        if (ll1.latitude<ll2.latitude)
        {
            minLat = ll1.latitude;
            maxLat = ll2.latitude;
        }
        else {
            minLat = ll2.latitude;
            maxLat = ll1.latitude;
        }
        if (ll1.longitude<ll2.longitude)
        {
            minLon = ll1.longitude;
            maxLon = ll2.longitude;
        }
        else {
            minLon = ll2.longitude;
            maxLon = ll1.longitude;
        }
        BoundingBox bb = new BoundingBox(minLat,
                minLon, maxLat, maxLon);
        return bb;
    }
    public static void resetMap(){


        for(Marker m: workerLocs.values()){
            mapView.getLayerManager().getLayers().remove(m);
        }
        for(TextMarker tm:workerTexts.values()){
            mapView.getLayerManager().getLayers().remove(tm);
        }

        workerLocs.clear();
        workerTexts.clear();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            //mBuilder.setOngoing(false);
            mNotifyMgr.cancelAll();
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

}


