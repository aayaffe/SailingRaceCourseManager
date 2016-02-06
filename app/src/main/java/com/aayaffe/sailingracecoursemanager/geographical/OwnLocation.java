package com.aayaffe.sailingracecoursemanager.geographical;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

/**
 * Created by aayaffe on 28/09/2015.
 */
public class OwnLocation implements IGeo,LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;
    private AviLocation mLastLocation = new AviLocation(0,0);
    private static String TAG = "OwnLocation";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Activity activity;
    private Context context;


    public OwnLocation (Context c){
        this.context = c;
        InitGPS(context);
    }

    @Override
    public AviLocation getLoc() {
        return mLastLocation;
        //return getLastBestLocation();
    }
    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

//        if (null != locationNet) {
//            NetLocationTime = locationNet.getTime();
//        }
        return locationGPS;
//        if ( 0 < GPSLocationTime - NetLocationTime ) {
//            return locationGPS;
//        }
//        else {
//            return locationNet;
//        }
    }
    private void InitGPS(Context c){
        buildGoogleApiClient();

        mGoogleApiClient.connect();
        locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);


    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "GPS Location: " + location);
        mLastLocation = GeoUtils.toAviLocation(location);
        Log.d(TAG, "OwnLocation: " + mLastLocation.toLocation());
        mLastLocation.lastUpdate = new Date();
    }
    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        //if (mRequestingLocationUpdates) {
        startLocationUpdates();
        //}
        mLastLocation = GeoUtils.toAviLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
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
}
