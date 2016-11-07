package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.BoatXmlParser;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseType;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseXmlParser;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 31/10/2016.
 */
public class MainCourseInputActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private ConfigChange configChange;
    SharedPreferences.Editor editor;

    public CourseXmlParser xmlParserC;
    public BoatXmlParser boatXmlParser;
    private List<CourseType> coursesInfo;
    private List<Boat> boats;


    private Button CourseButton;
    private Button DistanceButton;
    private Button WindDirButton;
    private Button applyB;

    private RaceCourse raceCourse;
    private Buoy myBoat = new Buoy("testMyBoat", new AviLocation(32.85,34.99));//TODO:????
    private static Map<String,String> courseOptions;
    private float dist2m1;

    private static OnMyCourseInputResult mInputResult;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_course_input_activity);


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(configChange);
        configChange = new ConfigChange();
        editor = sharedPreferences.edit();

        xmlParserC = new CourseXmlParser(this, "courses_file.xml");
        boatXmlParser = new BoatXmlParser(this, "boats_file.xml");
        coursesInfo = xmlParserC.parseCourseTypes();
        boats=boatXmlParser.parseBoats();

        CourseButton =(Button)findViewById(R.id.coursetype_input_button);
        CourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTypeDialog dialog = new CourseTypeDialog(context, coursesInfo);
                dialog.show();
                dialog.setDialogResult(new CourseTypeDialog.OnMyDialogResult() {
                    public void finish(Map<String, String> result) {
                        //something to do
                        //use the map of the selected race curse options
                        courseOptions=result;
                    }
                });
            }
        });

        DistanceButton =(Button)findViewById(R.id.distance_input_button);
        DistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceDialog dialog = new DistanceDialog(context , boats);
                dialog.show();
                dialog.setDialogResult(new DistanceDialog.OnMyDialogResult() {
                    public void finish(double result) {
                        //something to do
                        dist2m1 = (float) result;
                    }

                });
            }
        });


        applyB = (Button)findViewById(R.id.apply_race_course_input_button);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raceCourse = new RaceCourse(context,  myBoat.getAviLocation() , 315 ,dist2m1, (float) 0.11 ,courseOptions);  //defultStartLine: 200m
                finish();
            }
        });


    }

    public static void setOnMyCourseInputResult(OnMyCourseInputResult inputResult){
        mInputResult = inputResult;
    }

    public interface OnMyCourseInputResult {
        void finish(RaceCourse raceCourse);
    }
}
