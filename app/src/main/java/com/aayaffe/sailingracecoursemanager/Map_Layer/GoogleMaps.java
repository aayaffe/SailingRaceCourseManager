package com.aayaffe.sailingracecoursemanager.Map_Layer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.R;
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
public class GoogleMaps implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private static final String TAG = "OpenSeaMap";
    public GoogleMap mapView;
    private Context c;
    public BiMap<UUID, Marker> uuidToMarker = HashBiMap.create();
    public BiMap<UUID, String> uuidToId = HashBiMap.create();

    private Marker lastOpenned = null;
    public DialogFragment df;

    public void Init(Activity a, Context c, SharedPreferences sp) {
        this.c = c;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) ((FragmentActivity) a).getSupportFragmentManager()
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

    public void setCenter(Location l) {
        setCenter(GeoUtils.toLatLng(l));
    }

    public void setCenter(LatLng ll) {
        mapView.animateCamera(CameraUpdateFactory.newLatLng(ll));
    }

    public void addBuoy(Buoy buoy, String snippet) {
        if (uuidToMarker.containsKey(buoy.getUUID())) {
            Marker currentMarker;
            currentMarker = uuidToMarker.get(buoy.getUUID());
            currentMarker = updateBuoy(buoy, currentMarker);
            if (currentMarker.isInfoWindowShown()) {
                currentMarker.showInfoWindow();
            }
        } else if (mapView != null) {
            int resourceID = buoy.getIconResourceId();
            Marker marker = mapView.addMarker(new MarkerOptions().position(buoy.getLatLng()).icon(BitmapDescriptorFactory.fromResource(resourceID)).title(buoy.getName()).snippet(snippet));
            uuidToMarker.put(buoy.getUUID(), marker);
            uuidToId.put(buoy.getUUID(), marker.getId());
        }
    }

    public Marker addMark(Buoy ao, String caption, int resourceID) {
        Marker m;
        try {
            if (uuidToMarker.containsKey(ao.getUUID())) {
                m = uuidToMarker.get(ao.getUUID());
                boolean infoWindows = m.isInfoWindowShown();
                m = updateMark(ao, m, resourceID, caption);
                if (infoWindows) {
                    m.showInfoWindow();
                }
                return m;
            } else if (mapView != null) {
                m = mapView.addMarker(new MarkerOptions().position(ao.getLatLng()).title(ao.getName()).snippet(caption).icon(BitmapDescriptorFactory.fromResource(resourceID)));
                uuidToMarker.put(ao.getUUID(), m);
                uuidToId.put(ao.getUUID(), m.getId());
                return m;
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to add mark", e);
        }
        return null;
    }

    private Marker updateMark(Buoy ao, Marker m, int resourceID, String caption) {
        if (isValid(ao)) {
            m.setPosition(ao.getLatLng());
            m.setIcon(BitmapDescriptorFactory.fromResource(resourceID));
            m.setSnippet(caption);
            m.setRotation(ao.getAviLocation().cog);
        }
        return m;
    }

    private Marker updateBuoy(Buoy ao, Marker m) {
        if (isValid(ao)) {
            m.setPosition(ao.getLatLng());
            m.setSnippet(ao.getName());
            m.setRotation(ao.getAviLocation().cog);
        }
        return m;
    }

    private boolean isValid(Buoy ao) {
        return (ao != null) && (ao.getAviLocation() != null) && (ao.getName() != null) && (ao.getEnumBuoyType() != null) && (ao.getLastUpdate() != null);
    }

    public void removeMark(Marker m) {
        m.remove();
        GoogleMapsActivity.commManager.removeBuoyObject(m.getTitle());
        uuidToMarker.inverse().remove(m); //TODO: Check if works... else use uuidToID to obtain Uuid and delete
        uuidToId.inverse().remove(m.getId());
    }

    public void removeMark(UUID uuid) {
        Marker m = uuidToMarker.get(uuid);
        if (m != null) {
            m.remove();
            GoogleMapsActivity.commManager.removeBuoyObject(m.getTitle()); //TODO Check if mapping correct using title
            uuidToMarker.remove(uuid);
            uuidToId.remove(uuid);
        }
    }

    public void ZoomToMarks() {
        ArrayList<Marker> ms = new ArrayList<>(uuidToMarker.values());
        if (ms.size() == 1) {
            setCenter((ms.get(0).getPosition()));
            return;
        }
        if (ms.size() == 0) {
            return;
        }
        ZoomToBounds(ms);
    }

    public void ZoomToBounds(List<Marker> marks) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : marks) {
            builder.include(marker.getPosition());
        }
        try {
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //googleMap.moveCamera(cu);
            mapView.animateCamera(cu);
        } catch (Exception e) {
            Log.e(TAG,"Error zomming to bounds",e);
        }
    }

    public void removeAllMarks() {
        mapView.clear();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        try {
            boolean isBuoy = false;
            if (marker.getTitle().contains("BUOY")) isBuoy = true;
            if (isBuoy && GoogleMapsActivity.isCurrentEventManager()) {
                if (deleteMark) {
                    removeMark(marker);
                    deleteMark = false;
                } else {
                    Toast.makeText(c, "Press the buoys info window again to delete.", Toast.LENGTH_SHORT).show();
                    deleteMark = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteMark = false;
                        }
                    }, 3 * 1000);
                }

            }
        } catch (Exception e) {
            Log.d(TAG, "Failed on info click", e);
        }

    }

    private Boolean deleteMark = false;
}
