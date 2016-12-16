package com.aayaffe.sailingracecoursemanager.Initializing_Layer;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.R;

import java.util.List;

/**
 * Created by Jonathan on 13/07/2016.
 */
public class CourseType {
    private String name;
    private int imageID = R.drawable.racecourse_optimist;
    private List<LegsType> legsTypes;

    /*private List<String[]> options; //{name, view to contain options, option1, option2, ...}
    public double[] courseFactors = {1,1,0};*/


    public CourseType(String name, List<LegsType> legsTypes) {
        this.name = name;
        this.legsTypes = legsTypes;
    }

    public CourseType(String name) {
        this.name = name;
        this.legsTypes = null;
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

    public List<LegsType> getLegsTypes() {
        return legsTypes;
    }

    public void setLegsTypes(List<LegsType> legsTypes) {
        this.legsTypes = legsTypes;
    }

    public LegsType getLastLeg(){
        if(legsTypes.size()>0)return legsTypes.get(legsTypes.size()-1);
        else return null;
    }

    public void setLastLegCourseFactors(int windDirIndex, double factor){
        if(legsTypes.size()>0)legsTypes.get(legsTypes.size()-1).setCourseFactor(windDirIndex, factor);
        else Log.w("CourseType", "cannot use setLastLegCourseFactors: null legsTypes");
    }

    public String[] getLegsNames(){
        String[] names = new String[legsTypes.size()];
        for(int i=0; i<legsTypes.size(); i++){
            names[i]=legsTypes.get(i).getName();
        }
        return names;
    }
}
