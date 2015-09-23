package com.aayaffe.sailingracecoursemanager;

import java.io.File;
import java.util.List;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.labels.LabelLayer;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;

import com.aayaffe.sailingracecoursemanager.communication.CommStub;
import com.aayaffe.sailingracecoursemanager.communication.QuickBlox;

public class MainActivity extends Activity {

    // name of the map file in the external storage
    private static final String MAPFILE = "andorra.map";

    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private LocationManager locationManager;
    private QuickBlox qb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidGraphicFactory.createInstance(this.getApplication());

        this.mapView = new MapView(this);
        RelativeLayout rlMap = (RelativeLayout) findViewById(R.id.rlMap);
        rlMap.addView(mapView,0);
        //setContentView(this.mapView);

        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        this.mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        InitGPS();
        qb = new QuickBlox(this,getResources());
        qb.login("SRCMWorker1", "Aa123456z", "1");
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.mapView.getModel().mapViewPosition.setCenter(new LatLong(42.550111, 1.559907));
        this.mapView.getModel().mapViewPosition.setZoomLevel((byte) 12);

        // tile renderer layer using internal render theme
//        MapDataStore mapDataStore = new MapFile(getMapFile());
//        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
//                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
//        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        // only once a layer is associated with a mapView the rendering starts
        //this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        qb.sendLoc(null);


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
    public void InitGPS(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLoc() {
        return getLastBestLocation();
    }

    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }



    }
    public void getLocButtonOnClick(View v) {
        GraphicFactory gf = AndroidGraphicFactory.INSTANCE;
        Location l = qb.getLoc(1);
        if (l!=null) {
            Marker m = new Marker(new LatLong(l.getLatitude(), l.getLongitude()), AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.worker)), 0, 0);
            this.mapView.getLayerManager().getLayers().add(m);


        }
        Paint paintStroke = gf.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setStrokeWidth(7);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setDashPathEffect(new float[] { 25, 15 });
        paintStroke.setStrokeWidth(5);
        paintStroke.setStrokeWidth(3);
        Location myLocation = getLastBestLocation();
        if (myLocation!=null) {
            Marker m = new Marker(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()), AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.commodore)), 0, 0);
            this.mapView.getLayerManager().getLayers().add(m);
            this.mapView.getModel().mapViewPosition.setCenter(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));

        }
        if((l!=null)&&(myLocation!=null)){


                Polyline line = new Polyline(paintStroke,
                        AndroidGraphicFactory.INSTANCE);

                List<LatLong> geoPoints = line.getLatLongs();
                geoPoints.add(new LatLong(l.getLatitude(), l.getLongitude()));
                geoPoints.add(new LatLong(myLocation.getLatitude(), myLocation.getLongitude()));
                this.mapView.getLayerManager().getLayers().add(line);
            Paint p = gf.createPaint();
            p.setColor(Color.BLACK);
            p.setTextSize(50);
            TextMarker tm = new TextMarker(((int)myLocation.bearingTo(l)+360)+"\\"+((int)(myLocation.distanceTo(l)/1609.34))+"NM",p,new LatLong(l.getLatitude(), l.getLongitude()),AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.worker)));
            this.mapView.getLayerManager().getLayers().add(tm);
            }
        }

}

