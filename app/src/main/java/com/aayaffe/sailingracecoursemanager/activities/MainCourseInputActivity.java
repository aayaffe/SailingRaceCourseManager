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
import com.aayaffe.sailingracecoursemanager.general.ConfigChange;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.InitialCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
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
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 31/10/2016.
 */
public class MainCourseInputActivity extends Activity {

    private static final String TAG = "MainCourseInputActivity";
    private SharedPreferences sharedPreferences;
    private ConfigChange configChange;
    SharedPreferences.Editor editor;

    private List<RaceCourseDescriptor> coursesInfo;
    private List<Boat> boats;
    private IGeo iGeo;


    private static List<Double> courseFactors;
    private static Map<String,Boolean> courseOptions = new HashMap<>();
    private static RaceCourseDescriptor selectedRCD;
    private static float dist2m1 = 1;
    private static float startLineLength = 0.11f;
    private static float gateLength = 0.11f;
    private static float windDirection;
    private static double windSpeed = 15;

    private static OnMyCourseInputResult mInputResult;
    private Context context=this;
    private Legs legs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_course_input);
        IDBManager comm = GoogleMapsActivity.commManager;


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(configChange);
        configChange = new ConfigChange();
        windDirection = Float.parseFloat(sharedPreferences.getString("windDir", "90"));
        iGeo = new OwnLocation(getBaseContext(), this);
        coursesInfo = new InitialCourseDescriptor().getRaceCourseDescriptors();
        boats = comm.getBoatTypes();


        try{
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    legs = null;
                } else {
                    legs =(Legs) extras.getSerializable("LEGS");
                    selectedRCD = (RaceCourseDescriptor) extras.getSerializable("RCD");
                }
            } else {
                legs = (Legs) savedInstanceState.getSerializable("LEGS");
                selectedRCD = (RaceCourseDescriptor) savedInstanceState.getSerializable("RCD");
            }


        } catch (Exception e)
        {
            Log.e(TAG,"Unable to get extra legs",e);
        }
        if (selectedRCD==null){
            selectedRCD= coursesInfo.get(0);
        }
        if (legs == null){
            legs = selectedRCD.getRaceCourseLegs().get(0);
        }


        Button courseButton = (Button) findViewById(R.id.coursetype_input_button);
        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseTypeDialog dialog = new CourseTypeDialog(context, coursesInfo);
                dialog.show();
                dialog.setDialogResult(new CourseTypeDialog.OnMyDialogResult() {
                    @Override
                    public void finish(Map<String, Boolean> result, List<Double> factorResult, Legs legs, RaceCourseDescriptor rcd) {
                        selectedRCD = rcd;
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
                DistanceDialog dialog = new DistanceDialog(context , boats, legs);
                dialog.show();
                dialog.setDialogResult(new DistanceDialog.OnMyDialogResult() {
                    @Override
                    public void finish(double dist2M1,double startLine,double gate, double windSpeed) {
                        //something to do
                        dist2m1 = (float) dist2M1;
                        startLineLength = (float) GeoUtils.toNauticalMiles(startLine);
                        gateLength = (float) GeoUtils.toNauticalMiles(gate);
                        MainCourseInputActivity.windSpeed = windSpeed;
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

                RaceCourse rc = new RaceCourse(context,  GeoUtils.toAviLocation(iGeo.getLoc()) , (int)windDirection ,dist2m1, startLineLength, gateLength ,legs ,courseOptions);  //defultStartLine: 200m
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RACE_COURSE", rc);
                resultIntent.putExtra("LEGS", legs);
                resultIntent.putExtra("RCD", selectedRCD);
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
        Button courseStatisticsButton = (Button) findViewById(R.id.course_statistics_button);
        courseStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), RaceCourseStatisticsActivity.class);
                i.putExtra("Legs",legs);
                i.putExtra("RCD",selectedRCD);
                i.putExtra("Dist2m1",dist2m1);
                i.putExtra("WindSpeed",windSpeed);

                startActivity(i);
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
