package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.BuoyEditDialog;
import com.aayaffe.sailingracecoursemanager.BuoyInputDialog;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aayaffe on 04/10/2015.
 */
public class GoogleMaps implements GoogleMap.OnMapLongClickListener,GoogleMap.OnInfoWindowClickListener,OnMapReadyCallback {
    private static final String TAG = "OpenSeaMap";
    public GoogleMap mapView;
    private Context c;
    private Activity a;
    private LatLong lastTapLatLong;
    private SharedPreferences sp;
    private TileDownloadLayer downloadLayer;
    private HashMap<String, Marker> markers = new HashMap<>();
    private Marker lastOpenned = null;
    public DialogFragment df;

    public void Init(Activity a, Context c, SharedPreferences sp)
    {
        this.c =c;
        this.a = a;
        this.sp = sp;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity)a).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



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
        mapView = googleMap;
        Location center = new Location("Manual");
        center.setLatitude(32.831653);
        center.setLongitude(35.019216);

//        if ((iGeo.getLoc().lat != 0)||(iGeo.getLoc().lon!=0)) {
//            mapLayer.setCenter(GeoUtils.toLocation(iGeo.getLoc()));
//        }
        ZoomToMarks();

        setCenter(center);
        mapView.setOnMapLongClickListener(this);
        mapView.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });
        mapView.setOnInfoWindowClickListener(this);


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
        //mapView.
    }

    public void destroy(){

    }

    public Marker addMark(LatLng ll, String name,String caption, int ResourceID){
        if (ll==null) return null;
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
        GoogleMapsActivity.commManager.removeBueyObject(m.getTitle());
        markers.remove(m);
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
    public void ZoomToMarks(){
        ZoomToBounds(new ArrayList<>(markers.values()));
    }
    public void ZoomToBounds(List<Marker> marks){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : marks) {
            builder.include(marker.getPosition());
        }
        try{
            LatLngBounds bounds = builder.build();
            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //googleMap.moveCamera(cu);
            mapView.animateCamera(cu);
        } catch (Exception e){

        }


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





    public LatLong getLastTapLatLong() {
        return lastTapLatLong;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "Plus Fab Clicked");
        String t = marker.getTitle();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(t);
        m.find();
        long id = Integer.parseInt(m.group());
//        df = BuoyEditDialog.newInstance(id);
//        df.show(a.getFragmentManager(), "Edit_Buoy");
        if (marker.getTitle().contains("Buoy")){
            removeMark(marker);
        }
    }

    public void removeMark(long id) {
        removeMark(markers.get("Buoy" + id));

    }
}
