package com.aayaffe.sailingracecoursemanager.general;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.db.FirebaseDB;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 19/03/2016.
 */
public class Versioning {
    private static final String TAG = "Versioning";
    private Context c;
    private IDBManager commManager;

    public Versioning(Context c){
        this.c = c;
        commManager = new FirebaseDB(c);
    }

    public long getSupportedVersion(){
        try {
            return commManager.getSupportedVersion();
        }catch (Exception e){
            Log.e(TAG, "error getting supported version",e);
            return -1;
        }
    }

    public int getInstalledVersion(){
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            return pInfo.versionCode;
        }catch(PackageManager.NameNotFoundException e)
        {
            Log.d(TAG,"Error retreiving versioncode: ",e);
        }
        return -1;
    }
}
