package com.aayaffe.sailingracecoursemanager.calclayer;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseException;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 27/08/2016.
 */
public class RaceCourse implements Serializable{

    private static final String TAG = "RACECOURSE" ;
    /**
     * RaceCourse represents the actual race course, and serves:
     * -holding course input (windDir, Dist2m1, courseType, marks array)
     * turning input data into actual bouys
     *
     */
    private double dist2m1;  //distance to mark 1
    private int windDir;  //wind direction
    private AviLocation signalBoatLoc;
    private double startLineDist;
    private double gateLength;
    private Map<String, Boolean> selectedOptions;
    private List<DBObject> bouyList = new ArrayList<>();
    //private transient CourseXmlParser xmlParserC;
    private UUID raceCourseUUID;
    transient Context context;


    public RaceCourse(){
        //Empty constructor for Serializing to firebase
    }
    public RaceCourse(Context context, AviLocation signalBoatLoc, int windDirection, double distance2mark1 , double startLineLength, double gateLength,Legs l, Map<String, Boolean> selectedCourseOptions ){
        this.context = context;
        if(signalBoatLoc!=null)
            this.signalBoatLoc = signalBoatLoc;
        else
            this.signalBoatLoc = new AviLocation(32.85,3499);
        windDir=windDirection;
        dist2m1=distance2mark1;
        startLineDist=startLineLength;
        this.gateLength = gateLength;
        selectedOptions=selectedCourseOptions;
        raceCourseUUID = UUID.randomUUID();
        convertMarks2Buoys(l);
        Log.d("RaceCourse class note", "constructor done");
    }


//    private AviLocation referencePointLoc(){  //returns the RP location from signalBoatLoc
//        AviLocation startLineCenter  = new AviLocation(signalBoatLoc,windDir-90,startLineDist/2);
//        return new AviLocation(startLineCenter,windDir, 0.05);
//    }
    public synchronized List<DBObject> convertMarks2Buoys(Legs l){ //converts all data into the a list of BUOY class
        //Mark referenceMark = xmlParserC.parseMarks(selectedOptions);
        try {
            bouyList = l.parseBuoys(signalBoatLoc, dist2m1, windDir, (float) startLineDist, (float) gateLength, raceCourseUUID, selectedOptions);
        } catch(RaceCourseException e){
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Failed to parse buoys");
            crashlytics.recordException(e);
            Log.e(TAG,"Failed to parse buoys",e);
            Toast.makeText(context, R.string.error_adding_race_course, Toast.LENGTH_LONG).show();
        }
        return bouyList;
    }

    public List<DBObject> getBuoyList() {
        return bouyList;
    }
}
