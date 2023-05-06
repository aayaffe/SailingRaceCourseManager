package com.aayaffe.sailingracecoursemanager.Map_Layer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.activities.GoogleMapsActivity;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 04/10/2015.
 */
public class GoogleMaps implements GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener, OnMapReadyCallback {
    private static final String TAG = "GoogleMaps";
    public GoogleMap mapView;
    private Context c;
    public BiMap<UUID, Marker> uuidToMarker = HashBiMap.create();
    public BiMap<UUID, String> uuidToId = HashBiMap.create();
    private MapClickMethods clickMethods;
    private Marker lastOpenned = null;
    public DialogFragment df;
    private Polyline polyline;
    private Activity activity;

    public void Init(Activity a, Context c, SharedPreferences sp, MapClickMethods mcm) {
        this.clickMethods = mcm;
        this.c = c;
        this.activity = a;
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
            @Override
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
        mapView.setOnInfoWindowLongClickListener(this);
        mapView.setPadding(0, 170, 0, 0);
        //mapView.setPadding(GeneralUtils.convertDpToPixel(10,c), GeneralUtils.convertDpToPixel(70,c), GeneralUtils.convertDpToPixel(10,c), GeneralUtils.convertDpToPixel(80,c));

        boolean success = mapView.setMapStyle(MapStyleOptions.loadRawResourceStyle(c,R.raw.mapstyle_avi));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }


    }

    public void setCenter(Location l) {
        setCenter(GeoUtils.toLatLng(l));
    }

    public void setCenter(LatLng ll) {
        mapView.animateCamera(CameraUpdateFactory.newLatLng(ll));
    }

    public void addBuoy(DBObject buoy, String snippet) {
        if (uuidToMarker.containsKey(buoy.getUUID())) {
            Marker currentMarker = updateBuoy(buoy, uuidToMarker.get(buoy.getUUID()), snippet);
            if (currentMarker.isInfoWindowShown()) {
                currentMarker.showInfoWindow();
            }
        } else if (mapView != null) {
            int resourceID = buoy.getIconResourceId();
//            Marker marker = mapView.addMarker(new MarkerOptions().position(buoy.getLatLng()).icon(BitmapDescriptorFactory.fromResource(resourceID)).title(buoy.getName()).snippet(snippet));
            Marker marker = mapView.addMarker(
                    new MarkerOptions()
                            .position(buoy.getLatLng())
                            .icon(BitmapFromVector(c,resourceID))
                            .title(buoy.getName())
                            .anchor(0.5f, 0.5f)
                            .snippet(snippet));

            uuidToMarker.put(buoy.getUUID(), marker);
            uuidToId.put(buoy.getUUID(), marker.getId());
        }
    }
    private BitmapDescriptor
    BitmapFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Marker addMark(DBObject ao, String caption, int resourceID, int zIndex) {
        Marker m;
        try {
            if (uuidToMarker.containsKey(ao.getUUID())) {
                m = uuidToMarker.get(ao.getUUID());
                boolean infoWindows = m.isInfoWindowShown();
                m = updateMark(ao, m, caption,resourceID,zIndex);
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

    private Marker updateMark(DBObject ao, Marker m, String caption, int resourceID, int zIndex) {
        if (isValid(ao)) {
            m.setIcon(BitmapDescriptorFactory.fromResource(resourceID));
            m.setZIndex(zIndex);
            m.setPosition(ao.getLatLng());
            m.setSnippet(caption);
            m.setRotation(ao.getAviLocation().cog);
        }
        return m;
    }
    //TODO: merge different marker adding functions!
    private Marker updateBuoy(DBObject ao, Marker m, String caption) {
        if (isValid(ao)) {
            m.setPosition(ao.getLatLng());
            m.setSnippet(caption);
            m.setRotation(ao.getAviLocation().cog);
        }
        return m;
    }

    private boolean isValid(DBObject ao) {
        return (ao != null) && (ao.getAviLocation() != null) && (ao.getName() != null) && (ao.getBuoyType() != null) && (ao.getLastUpdate() != null);
    }


    public void removeMark(UUID uuid, boolean removeFromDB) {
        Marker m = uuidToMarker.get(uuid);
        if (m != null) {
            m.remove();
            if (removeFromDB) {
                GoogleMapsActivity.getCommManager().removeBuoyObject(uuid.toString());
            }
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
        if (ms.isEmpty()) {
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
            float width = GeneralUtils.getDeviceWidth(activity)-40;
            float height = GeneralUtils.getDeviceHeight(activity)-GeneralUtils.convertDpToPixel(150,c);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, (int)width, (int)height, 0);
            mapView.animateCamera(cu);
        } catch (Exception e) {
            Log.e(TAG,"Error zomming to bounds",e);
        }
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        clickMethods.infoWindowClick(uuidToMarker.inverse().get(marker));
    }
    @Override
    public void onInfoWindowLongClick(Marker marker) {
        try {
            Log.d(TAG,"onInfoWindowLongClick");
            clickMethods.infoWindowLongClick(uuidToMarker.inverse().get(marker));

        } catch (Exception e) {
            Log.d(TAG, "Failed on info click", e);
        }
    }

    /**
     * Adds a line between a and b
     * Removes a previous line if one exists
     * Removes the existing line if a or b are null.
     * @param a
     * @param b
     */
    public void addLine(UUID a, UUID b) {
        if(a==null||b==null){
            if (polyline!=null)
                polyline.remove();
            return;
        }
        Marker m1 = uuidToMarker.get(a);
        if (m1==null) {
            if (polyline!=null)
                polyline.remove();
            return;
        }
        LatLng a1 = m1.getPosition();
        Marker m2 = uuidToMarker.get(b);
        LatLng b1;
        if (m2==null)
            return;

        b1 = m2.getPosition();
        PolylineOptions po = new PolylineOptions()
                .add(a1)
                .add(b1);

        if (polyline!=null)
            polyline.remove();
        polyline = mapView.addPolyline(po);

    }
    private Boolean deleteMark = false;

    public void setZoom(int i) {
        CameraUpdate cu = CameraUpdateFactory.zoomTo(i);
        mapView.animateCamera(cu);
    }

    /***
     * Sets the zoom level and centers the map.
     * @param i zoom level (0 - worldwide, 10 cite wide)
     * @param l center location
     */
    public void setZoom(int i, Location l) {
        if (l==null)
            return;
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(GeoUtils.toLatLng(l),i);
        mapView.animateCamera(cu);
    }
}
