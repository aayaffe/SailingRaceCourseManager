package com.aayaffe.sailingracecoursemanager.geographical;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 28/09/2015.
 */
public class OwnLocation implements IGeo, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private LocationListener ll = null;
    private Location mLastLocation;
    private static String TAG = "OwnLocation";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Activity activity;
    private Context context;
    private long mLastLocationMillis;
    private boolean isGPSFix;
    private boolean gpsInitialised = false;


    public OwnLocation(Context c, Activity a) {
        this.context = c;
        this.activity = a;
        InitGPS(context);
    }
    public OwnLocation(Context c, Activity a, LocationListener ll) {
        this.context = c;
        this.activity = a;
        this.ll = ll;
        InitGPS(context);
    }

    @Override
    public Location getLoc() {
        if (!gpsInitialised)
            InitGPS(context);
        return mLastLocation;
    }
    /***
     * Requests persmission for location updates
     * @return true if location permission were granted
     */
    private boolean checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return false;
            }
        return true;
    }


    private void InitGPS(Context c) {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (checkForLocationPermission()){
            //Deprecated in API 24 only. wait for a while before replacing
            locationManager.addGpsStatusListener(new MyGPSListener());
            gpsInitialised = true;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            return;
        mLastLocation = location;
        mLastLocationMillis = SystemClock.elapsedRealtime();
//        if (ll!= null)
//            ll.onLocationChanged(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public boolean isGPSFix() {
        isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
        return isGPSFix;
    }

    private class MyGPSListener implements GpsStatus.Listener {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    if (mLastLocation != null)
                        isGPSFix();
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    isGPSFix = true;
                    break;
            }
        }
    }
}
