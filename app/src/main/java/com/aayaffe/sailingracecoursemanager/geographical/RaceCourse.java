package com.aayaffe.sailingracecoursemanager.geographical;

import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aayaffe on 21/03/2016.
 */
public class RaceCourse {
    private int windDir;
    private double windSpeed;
    private BoatType boatType;
    private int numOfBoats;
    private int goalTime;
    private AviLocation signalBoatLoc;
    private boolean isUpdated;
    private Marks marks = new Marks();




    public enum RaceCourseTypes{
        WINDWARD_LEEWARD_TRIANGLE,
        WINDWARD_LEEWARD,
        TRAPEZOID
    }
    public enum BoatType{ //TODO move to online database
        C470, C420, LASERRADIAL, LASERSTANDARD, LASER47, OPTIMISTINT, OPTIMISTLOCAL, RSX, BIC78, BIC68, BIC5, KITE, CATAMARAN17, CATAMARAN21, YACHT
    }

    public void calculateCourse(){
        addPinEndMark(4.7,numOfBoats,1.5,signalBoatLoc,windDir); //TODO obtain details from DB
    }
    public int calculateTargetSpeed(BoatType bt){
        //TODO Implement
        return 0;
    }

    private void addPinEndMark(double boatLength, int numOfBoats, double multiplyingFactor, AviLocation signalBoatLoc, int windDir){
        int length = calculateStartLineLength(boatLength,numOfBoats, multiplyingFactor);
        int dir = GeoUtils.relativeToTrueDirection(windDir, -90);
        AviLocation l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(signalBoatLoc.toLocation(), (float) dir, length));
        AviObject m = new AviObject();
        m.type = ObjectTypes.Buoy;
        m.name = "PinEnd";
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);

    }

    /**
     *
     * @param boatLength
     * @param numOfBoats
     * @param multiplyingFactor
     * @return The start line length
     */

    public int calculateStartLineLength(double boatLength, int numOfBoats, double multiplyingFactor){
        return (int)(boatLength*numOfBoats*multiplyingFactor);
    }




    public int getWindDir() {
        return windDir;
    }
    public void setWindDir(int windDir) {
        this.windDir = windDir;
    }
    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    public BoatType getBoatType() {
        return boatType;
    }
    public void setBoatType(BoatType boatType) {
        this.boatType = boatType;
    }
    public int getNumOfBoats() {
        return numOfBoats;
    }
    public void setNumOfBoats(int numOfBoats) {
        this.numOfBoats = numOfBoats;
    }
    public int getGoalTime() {
        return goalTime;
    }
    public void setGoalTime(int goalTime) {
        this.goalTime = goalTime;
    }
    public AviLocation getSignalBoatLoc() {
        return signalBoatLoc;
    }
    public void setSignalBoatLoc(AviLocation signalBoatLoc) {
        this.signalBoatLoc = signalBoatLoc;
    }
    public boolean isUpdated() {
        return isUpdated;
    }
    public Marks getMarks() {
        return marks;
    }
}
