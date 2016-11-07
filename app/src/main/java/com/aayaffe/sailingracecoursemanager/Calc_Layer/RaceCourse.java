package com.aayaffe.sailingracecoursemanager.Calc_Layer;
import android.content.Context;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseXmlParser;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 27/08/2016.
 */
public class RaceCourse {
    /**
     * RaceCourse represents the actual race course, and serves:
     * -holding course input (windDir, Dist2m1, courseType, marks array)
     * turning input data into actual bouys
     *
     */
    private static double dist2m1;  //distance to mark 1
    private static int windDir;  //wind direction
    private static Mark referenceMark;  //a mark that contains(like a tree) all marks
    private static AviLocation signalBoatLoc;
    private static float startLineDist;
    private static Map<String, String> selectedOptions;
    private static List<Buoy> bouyList = new ArrayList<Buoy>();
    public static CourseXmlParser xmlParserC;
    Context context;

    public RaceCourse(){
        //Empty constructor for Serializing to firebase
    }

    public RaceCourse(Context context, AviLocation signalBoatLoc, int windDirection, float distance2mark1 , float startLineDistance,Map<String, String> selectedCourseOptions ){
        this.context = context;
        if(signalBoatLoc!=null) RaceCourse.signalBoatLoc =signalBoatLoc;
        else RaceCourse.signalBoatLoc =new AviLocation(32.85,3499);
        windDir=windDirection;
        dist2m1=distance2mark1;
        startLineDist=startLineDistance;
        selectedOptions=selectedCourseOptions;
        xmlParserC = new CourseXmlParser(context, "courses_file.xml");
        convertMarks2Buoys();
        Log.d("RaceCourse class note", "constructor done");
    }


    private static AviLocation referencePointLoc(){  //returns the RP location from signalBoatLoc
        AviLocation startLineCenter  = new AviLocation(signalBoatLoc,windDir-90,startLineDist/2);
        return new AviLocation(startLineCenter,windDir, 0.05);
    }
    static synchronized public List<Buoy> convertMarks2Buoys(){ //converts all data into the a list of BUOY class
        referenceMark = xmlParserC.parseMarks(selectedOptions);  //TODO: is selectedOptions null?
        bouyList = referenceMark.parseBuoys(referencePointLoc(), dist2m1, windDir);
        return bouyList;
    }

    public List<Buoy> getBuoyList() {
        return bouyList;
    }
}
