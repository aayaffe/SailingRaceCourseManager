package com.aayaffe.sailingracecoursemanager.initializinglayer;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.R;
import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by Jonathan on 13/07/2016.
 */
public class RaceCourseDescriptor {
    private String name;
    private int imageID = R.drawable.racecourse_optimist;
    private List<RaceCourseLeg> raceCourseLegs;

    public RaceCourseDescriptor(){
        //Empty constructor for Firebase
    }
    public RaceCourseDescriptor(String name, List<RaceCourseLeg> raceCourseLegs) {
        this.name = name;
        this.raceCourseLegs = raceCourseLegs;
    }

    public RaceCourseDescriptor(String name) {
        this.name = name;
        this.raceCourseLegs = null;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
    public int getImageID() {
        return imageID;
    }

    public List<RaceCourseLeg> getRaceCourseLegs() {
        return raceCourseLegs;
    }
    public void setRaceCourseLegs(List<RaceCourseLeg> raceCourseLegs) {
        this.raceCourseLegs = raceCourseLegs;
    }

    @Exclude
    public RaceCourseLeg getLastLeg(){
        if(raceCourseLegs.size()>0)
            return raceCourseLegs.get(raceCourseLegs.size()-1);
        else return null;
    }

    @Exclude
    public String[] getLegsNames(){
        String[] names = new String[raceCourseLegs.size()];
        for(int i = 0; i< raceCourseLegs.size(); i++){
            names[i]= raceCourseLegs.get(i).getName();
        }
        return names;
    }
}
