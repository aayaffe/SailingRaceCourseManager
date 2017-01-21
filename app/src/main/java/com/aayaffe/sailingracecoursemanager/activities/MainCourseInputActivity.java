package com.aayaffe.sailingracecoursemanager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aayaffe.sailingracecoursemanager.calclayer.RaceCourse;
import com.aayaffe.sailingracecoursemanager.ConfigChange;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.InitialCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.CourseXmlParser;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.ICommManager;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.geographical.IGeo;
import com.aayaffe.sailingracecoursemanager.geographical.OwnLocation;
import com.aayaffe.sailingracecoursemanager.dialogs.CourseTypeDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.DistanceDialog;
import com.aayaffe.sailingracecoursemanager.dialogs.WindDirDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 31/10/2016.
 */
public class MainCourseInputActivity extends Activity {

    private static final String TAG = "MainCourseInputActivity";
    private SharedPreferences sharedPreferences;
    private ConfigChange configChange;
    SharedPreferences.Editor editor;

    private List<RaceCourseDescriptor2> coursesInfo;
    private List<Boat> boats;
    private IGeo iGeo;


    private static List<Double> courseFactors;
    private static Map<String,Boolean> courseOptions;
    private static float dist2m1 = 1;
    private static float startLineLength = 0.11f;

    private static float windDirection;

    private static OnMyCourseInputResult mInputResult;
    private Context context=this;
    private Legs legs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_course_input);
        ICommManager comm = GoogleMapsActivity.commManager;
        //Map<String,String> defaultCourseOptions  = new HashMap<>();
        //defaultCourseOptions.put("type","Windward-Leeward");
        //defaultCourseOptions.put("Legs","L-Leeward");
        //courseOptions=;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(configChange);
        configChange = new ConfigChange();
        windDirection = Float.parseFloat(sharedPreferences.getString("windDir", "90"));
        iGeo = new OwnLocation(getBaseContext(), this);
        CourseXmlParser xmlParserC = new CourseXmlParser(this, "courses_file.xml");
        coursesInfo = new InitialCourseDescriptor().getRaceCourseDescriptors();
        boats = comm.getBoatTypes();

        Button courseButton = (Button) findViewById(R.id.coursetype_input_button);
        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTypeDialog dialog = new CourseTypeDialog(context, coursesInfo);
                dialog.show();
                dialog.setDialogResult(new CourseTypeDialog.OnMyDialogResult() {
                    @Override
                    public void finish(Map<String, Boolean> result, List<Double> factorResult, Legs legs) {
                        courseOptions=result;
                        courseFactors=factorResult;
                        MainCourseInputActivity.this.legs = legs;
                    }
                });
            }
        });

        Button distanceButton = (Button) findViewById(R.id.distance_input_button);
        distanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DistanceDialog dialog = new DistanceDialog(context , boats, courseFactors);
                dialog.show();
                dialog.setDialogResult(new DistanceDialog.OnMyDialogResult() {
                    @Override
                    public void finish(double result,double startLine) {
                        //something to do
                        dist2m1 = (float) result;
                        startLineLength = (float) startLine;
                    }

                });
            }
        });

        Button windDirButton = (Button) findViewById(R.id.winddir_input_button);
        windDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindDirDialog dialog = new WindDirDialog(context, windDirection);
                dialog.show();
                dialog.setDialogResult(new WindDirDialog.OnMyDialogResult() {
                    @Override
                    public void finish(float windDir) {
                        windDirection= windDir;

                    }
                });
            }
        });


        Button applyB = (Button) findViewById(R.id.apply_race_course_input_button);
        applyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainCourseInput","dist2m1 = "+dist2m1);
                editor = sharedPreferences.edit();
                editor.putString("windDir",String.valueOf(windDirection));
                editor.apply();

                RaceCourse rc = new RaceCourse(context,  GeoUtils.toAviLocation(iGeo.getLoc()) , (int)windDirection ,dist2m1, startLineLength ,legs ,courseOptions);  //defultStartLine: 200m
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RACE_COURSE", rc);
                setResult(-1, resultIntent);
                finish();
            }
        });
        Button cancelB = (Button) findViewById(R.id.cancel_race_course_input_button);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"dist2m1 = "+dist2m1);
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
