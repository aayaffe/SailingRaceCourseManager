package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.Calc_Layer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.BoatXmlParser;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseType;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.CourseXmlParser;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.LegsType;
import com.aayaffe.sailingracecoursemanager.Map_Layer.GoogleMapsActivity;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aayaffe.sailingracecoursemanager.Map_Layer.GoogleMapsActivity.NEW_RACE_COURSE_REQUEST;

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
    private IGeo iGeo;


    private Button CourseButton;
    private Button DistanceButton;
    private Button WindDirButton;
    private Button applyB;

    private static RaceCourse raceCourse;
    private static double[] courseFactors;
    private static Map<String,String> courseOptions;
    private static float dist2m1 = 1;
    private static int windDirection;

    private static OnMyCourseInputResult mInputResult;
    private Context context=this;
    private ICommManager comm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_course_input_activity);
        comm = GoogleMapsActivity.commManager;
        Map<String,String> defaultCourseOptions  =new HashMap<String, String>();
        defaultCourseOptions.put("type","Windward-Leeward");
        defaultCourseOptions.put("Legs","L-Leeward");
        courseOptions=defaultCourseOptions;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(configChange);
        configChange = new ConfigChange();
        editor = sharedPreferences.edit();
        iGeo = new OwnLocation(getBaseContext(), this);
        xmlParserC = new CourseXmlParser(this, "courses_file.xml");
        boatXmlParser = new BoatXmlParser(this, "boats_file.xml");
        coursesInfo = xmlParserC.parseCourseTypes();
        //boats=boatXmlParser.parseBoats();
        boats = comm.getBoatTypes();

        CourseButton =(Button)findViewById(R.id.coursetype_input_button);
        CourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTypeDialog dialog = new CourseTypeDialog(context, coursesInfo);
                dialog.show();
                dialog.setDialogResult(new CourseTypeDialog.OnMyDialogResult() {
                    @Override
                    public void finish(Map<String, String> result, double[] factorResult) {
                        courseOptions=result;
                        courseFactors=factorResult;
                    }
                });
            }
        });

        DistanceButton =(Button)findViewById(R.id.distance_input_button);
        DistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceDialog dialog = new DistanceDialog(context , boats, courseFactors);
                dialog.show();
                dialog.setDialogResult(new DistanceDialog.OnMyDialogResult() {
                    public void finish(double result) {
                        //something to do
                        dist2m1 = (float) result;
                    }

                });
            }
        });

        WindDirButton = (Button)findViewById(R.id.winddir_input_button);
        WindDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindDirDialog dialog = new WindDirDialog(context, Float.parseFloat(sharedPreferences.getString("windDir", "90")));
                dialog.show();
                dialog.setDialogResult(new WindDirDialog.OnMyDialogResult() {
                    @Override
                    public void finish(double windDir) {
                        windDirection=(int)windDir;
                        editor.putString("windDir",String.valueOf(windDirection));
                        editor.apply();
                    }
                });
            }
        });


        applyB = (Button)findViewById(R.id.apply_race_course_input_button);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainCourseInput","dist2m1 = "+dist2m1);
                raceCourse = new RaceCourse(context,  GeoUtils.toAviLocation(iGeo.getLoc()) , windDirection ,dist2m1, (float) 0.11 ,courseOptions);  //defultStartLine: 200m
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RACE_COURSE", raceCourse);
                setResult(-1, resultIntent);
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
