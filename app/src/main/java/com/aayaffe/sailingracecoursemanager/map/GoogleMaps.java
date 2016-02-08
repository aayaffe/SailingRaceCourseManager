package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;

import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.renderer.TileRendererLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aayaffe on 04/10/2015.
 */
public class GoogleMaps implements GoogleMap.OnMapLongClickListener {
    private static final String TAG = "OpenSeaMap";
    public GoogleMap mapView;
    private Context c;
    private Activity a;
    private LatLong lastTapLatLong;
    private SharedPreferences sp;
    private TileDownloadLayer downloadLayer;
    private HashMap<String, Marker> markers = new HashMap<>();


    protected String getPersistableId() {
        return this.getClass().getSimpleName();
    }
    protected float getScreenRatio() {
        return 1.0F;
    }
    protected void createLayers() {

    }


    public void Init(Activity a, Context c, GoogleMap mv, SharedPreferences sp, Location center, int zoom)
    {
        this.c =c;
        this.a = a;
        mapView = mv;
        this.sp = sp;
        setCenter(center);
        createLayers();
        mapView.setOnMapLongClickListener(this);
    }


    public void setCenter(Location l) {
        setCenter(GeoUtils.toLatLong(l));
    }

    public void setCenter(LatLong ll){
        setCenter(GeoUtils.toLatLng(ll));
    }
    public void setCenter(LatLng ll){
        mapView.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    public void setCenter(double lat, double lon) {
        setCenter(new LatLong(lat, lon));
    }

    public void setZoomLevel(int zoom){

    }

    public void destroy(){

    }

    public Marker addMark(LatLng ll, String name,String caption, int ResourceID){
        if (markers.containsKey(name)){
            Marker m = markers.get(name);
            m.setPosition(ll);
            m.setIcon(BitmapDescriptorFactory.fromResource(ResourceID));
            m.setSnippet(caption);
            return m;
        }
        Marker m = mapView.addMarker(new MarkerOptions().position(ll).title(name).snippet(caption).icon(BitmapDescriptorFactory.fromResource(ResourceID)));
        markers.put(name,m);
        return m;

    }

    public boolean contains(Marker m){
//        return mapView.getLayerManager().getLayers().contains(m);
        return true;
    }

    public void removeMark(Marker m){
        m.remove();
    }
    private void zoomToBounds(LatLong ll1, LatLong ll2)
    {
//        BoundingBox bb = getBoundingBox(ll1,ll2);
//        Dimension dimension = this.mapView.getModel().mapViewDimension.getDimension();
//        this.mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
//                bb.getCenterPoint(),
//                LatLongUtils.zoomForBounds(dimension, bb, this.mapView.getModel().displayModel.getTileSize())));
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



    public void loadMap(){
//        // tile renderer layer using internal render theme
//        MapDataStore mapDataStore = getMapFile();
//        this.tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
//                this.mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
//        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
////         only once a layer is associated with a mapView the rendering starts
//        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
    }



    @Override //TODO Allow adding from outside the class
    public void onMapLongClick(LatLng point) {
        mapView.addMarker(new MarkerOptions()
                .position(point)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    protected File getMapFileDirectory() {
        return Environment.getExternalStorageDirectory();
    }



    public LatLong getLastTapLatLong() {
        return lastTapLatLong;
    }

}
