//package com.aayaffe.sailingracecoursemanager.initializinglayer;
//import android.util.Log;
//
//import com.google.firebase.database.Exclude;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by Jonathan on 22/08/2016.
// */
//
///*
//    legType represent a sub-type of a race course. for example, the trapezoid-"reach is half beat".
//    the legTypes class is designed to contain special options for every legType
// */
//public class RaceCourseLeg {
//    private static final String TAG = "RaceCourseLeg";
//    private String name;
//    private List<List<String>> options= new ArrayList<>(); //{name, view to contain options, option1, option2, ...}
//    private List<Double> courseFactors = new ArrayList<>(Arrays.asList(1d,1d,0d));
//
//    public RaceCourseLeg(){
//        //Empty constructor for Firebase
//    }
//    public RaceCourseLeg(String name){
//        this.name=name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    /**
//     *
//     * @return a name to represent the legType
//     */
//    public String getName() {
//        return name;
//    }
//    /**
//     *
//     * @return the list of possible options (gates...)
//     */
//    public List<List<String>> getOptions() {
//        return options;
//    }
//    public void setOptions(List<List<String>> options) {
//        this.options = options;
//    }
//    /**
//     * double[] courseFactors - array of the relations between the sum of every leg(upwind, downwind, reach). 1 is equal to the 1st leg length
//     */
//    public void setCourseFactors(List<Double> courseFactors) {
//        this.courseFactors = courseFactors;
//    }
//    public List<Double> getCourseFactors() {
//        return courseFactors;
//    }
//    @Exclude
//    public void setCourseFactor(int index, double factor){
//        if(index<courseFactors.size())
//            courseFactors.set(index,factor);
//        else Log.w(TAG, "setCourseFactor: out of factors");
//
//    }
//}
