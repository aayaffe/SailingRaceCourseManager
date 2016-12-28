package com.aayaffe.sailingracecoursemanager.initializinglayer;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 22/08/2016.
 */

/*
    legType represent a sub-type of a race course. for example, the trapezoid-"reach is half beat".
    the legTypes class is designed to contain special options for every legType
 */
public class RaceCourseLeg {
    private String name;
    private List<String[]> options= new ArrayList<>(); //{name, view to contain options, option1, option2, ...}
    private double[] courseFactors = {1,1,0};

    public RaceCourseLeg(){
        //Empty constructor for Firebase
    }
    public RaceCourseLeg(String name){
        this.name=name;
    }
    public void setName(String name) {
        this.name = name;
    }
    /**
     *
     * @return a name to represent the legType
     */
    public String getName() {
        return name;
    }
    /**
     *
     * @return the list of possible options (gates...)
     */
    public List<String[]> getOptions() {
        return options;
    }
    public void setOptions(List<String[]> options) {
        this.options = options;
    }
    /**
     * double[] courseFactors - array of the relations between the sum of every leg(upwind, downwind, reach). 1 is equal to the 1st leg length
     */
    public void setCourseFactors(double[] courseFactors) {
        this.courseFactors = courseFactors;
    }
    public double[] getCourseFactors() {
        return courseFactors;
    }
    @Exclude
    public void setCourseFactor(int index, double factor){
        if(index<courseFactors.length)
            courseFactors[index]=factor;
        else Log.w("CourseType class", "setCourseFactor: out of factors");

    }
}
