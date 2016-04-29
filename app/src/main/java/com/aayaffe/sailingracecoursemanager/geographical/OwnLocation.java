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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by aayaffe on 28/09/2015.
 */
public class OwnLocation implements IGeo,LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private LocationManager locationManager;
    private Location mLastLocation;// = new Location("New);
    private static String TAG = "OwnLocation";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Activity activity;
    private Context context;
    private long mLastLocationMillis;
    private boolean isGPSFix;



    public OwnLocation (Context c){
        this.context = c;
        InitGPS(context);

    }

    @Override
    public Location getLoc() {
        return mLastLocation;
    }
//    private Location getLastBestLocation() {
//        if (checkForLocationPermission())
//        {
//            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            long GPSLocationTime = 0;
//            if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }
//            long NetLocationTime = 0;
//            return locationGPS;
//        }
//
//       return null;
//
//    }

    /***
     *
     * @return true if location permission were granted
     */
    private boolean checkForLocationPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(context, R.string.ask_perm_location,Toast.LENGTH_LONG).show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                return false;
            }
        }
        return true;
    }
//    @Override //TODO check where is this derived from
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
    private void InitGPS(Context c){
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(new MyGPSListener());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        mLastLocation = location;
        mLastLocationMillis = SystemClock.elapsedRealtime();
    }
    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        //if (mRequestingLocationUpdates) {
        startLocationUpdates();
        //}
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void startLocationUpdates() {
        if (mGoogleApiClient.isConnected() ){
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
        {LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);}
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
