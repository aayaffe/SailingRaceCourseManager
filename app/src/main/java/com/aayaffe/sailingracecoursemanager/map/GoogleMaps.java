package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 04/10/2015.
 */
public class GoogleMaps implements GoogleMap.OnInfoWindowClickListener,OnMapReadyCallback {
    private static final String TAG = "OpenSeaMap";
    public GoogleMap mapView;
    private Context c;
    public BiMap<UUID,Marker> uuidToMarker = HashBiMap.create();
    public BiMap<UUID,String> uuidToId = HashBiMap.create();

    private Marker lastOpenned = null;
    public DialogFragment df;

    public void Init(Activity a, Context c, SharedPreferences sp) {
        this.c =c;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity)a).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
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
        ZoomToMarks();
        setCenter(GeoUtils.toLatLng(center));
        mapView.getUiSettings().setRotateGesturesEnabled(false);
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
        mapView.setPadding(0, 170, 0, 0);


    }
    public void setCenter(Location l){
        setCenter(GeoUtils.toLatLng(l));
    }
    public void setCenter(LatLng ll){
        mapView.animateCamera(CameraUpdateFactory.newLatLng(ll));
    }
    public Marker addMark(AviObject ao,String caption, int resourceID){
        Marker m;
        try {
            if (uuidToMarker.containsKey(ao.getUuid())) {
                m = uuidToMarker.get(ao.getUuid());
                boolean infoWindows=m.isInfoWindowShown();
                m = updateMark(ao, m, resourceID, caption);
                if (infoWindows) {
                    m.showInfoWindow();
                }
                return m;
            } else if (mapView!=null) {
                m = mapView.addMarker(new MarkerOptions().position(ao.getLatLng()).title(ao.name).snippet(caption).icon(BitmapDescriptorFactory.fromResource(resourceID)));
                uuidToMarker.put(ao.getUUID(),m);
                uuidToId.put(ao.getUUID(),m.getId());
                return m;
            }
        } catch(Exception e){
            Log.d(TAG,"Failed to add mark",e);
        }
        return null;
    }
    private Marker updateMark(AviObject ao, Marker m, int resourceID, String caption) {
        if (isValid(ao)){
            m.setPosition(ao.getLatLng());
            m.setIcon(BitmapDescriptorFactory.fromResource(resourceID));
            m.setSnippet(caption);
            m.setRotation(ao.getAviLocation().cog);
        }
        return m;
    }
    private boolean isValid(AviObject ao) {
        return (ao != null) && (ao.getAviLocation() != null) && (ao.name != null) && (ao.getType() != null) && (ao.lastUpdate != null);
    }
    public void removeMark(Marker m){
        m.remove();
        GoogleMapsActivity.commManager.removeBueyObject(m.getTitle());
        uuidToMarker.inverse().remove(m); //TODO: Check if works... else use uuidToID to obtain Uuid and delete
        uuidToId.inverse().remove(m.getId());
    }
    public void removeMark(UUID uuid){
        Marker m = uuidToMarker.get(uuid);
        if(m!=null) {
            m.remove();
            GoogleMapsActivity.commManager.removeBueyObject(m.getTitle()); //TODO Check if mapping correct using title
            uuidToMarker.remove(uuid);
            uuidToId.remove(uuid);
        }
    }
    public void ZoomToMarks(){
        ArrayList<Marker> ms = new ArrayList<>(uuidToMarker.values());
        if (ms.size()==1) {
            setCenter((ms.get(0).getPosition()));
            return;
        }
        if (ms.size()==0){
            return;
        }
        ZoomToBounds(ms);
    }
    public void ZoomToBounds(List<Marker> marks){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : marks) {
            builder.include(marker.getPosition());
        }
        try{
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //googleMap.moveCamera(cu);
            mapView.animateCamera(cu);
        } catch (Exception e){

        }
    }
    public void removeAllMarks(){
        mapView.clear();
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        try{
            boolean isBuoy = false;
            if (marker.getTitle().contains("Buoy")) isBuoy=true;
            if (isBuoy && GoogleMapsActivity.isCurrentEventManager()){
                if (deleteMark){
                    removeMark(marker);
                    deleteMark = false;
                }
                else {
                    Toast.makeText(c, "Press the buoys info window again to delete.", Toast.LENGTH_SHORT).show();
                    deleteMark = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteMark = false;
                        }
                    }, 3 * 1000);
                }

            }}catch(Exception e){
            Log.d(TAG,"Failed on info click", e);
        }

    }
    private Boolean deleteMark = false;
}
