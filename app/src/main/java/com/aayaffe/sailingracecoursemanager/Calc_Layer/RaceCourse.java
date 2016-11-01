package com.aayaffe.sailingracecoursemanager.Calc_Layer;
import android.content.Context;

import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseXmlParser;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

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
    private static List<Buoy> bouyList;
    public static CourseXmlParser xmlParserC;
    Context context;

    public RaceCourse(){
    }

    public RaceCourse(Context context, AviLocation signalBoatLoc, int windDirection, float distance2mark1 , float startLineDistance,Map<String, String> selectedCourseOptions ){
        this.context = context;
        if(signalBoatLoc!=null)this.signalBoatLoc=signalBoatLoc;
        else this.signalBoatLoc=new AviLocation(32.85,3499);
        this.windDir=windDirection;
        this.dist2m1=distance2mark1;
        this.startLineDist=startLineDistance;
        this.selectedOptions=selectedCourseOptions;
        xmlParserC = new CourseXmlParser(context, "courses_file.xml");
        convertMarks2Buoys();
    }


    private AviLocation referencePointLoc(){  //returns the RP location from signalBoatLoc
        AviLocation startLineCenter  = new AviLocation(signalBoatLoc,windDir-90,startLineDist/2);
        return new AviLocation(startLineCenter,windDir, 0.05);
    }
    public List<Buoy> convertMarks2Buoys(){ //converts all data into the a list of Buoy class
        referenceMark = xmlParserC.parseMarks(selectedOptions);  //TODO: is selectedOptions null?
        bouyList = referenceMark.parseBuoys(referencePointLoc(), dist2m1, windDir);
        return bouyList;
    }

    public List<Buoy> getBuoyList() {
        return bouyList;
    }
}
