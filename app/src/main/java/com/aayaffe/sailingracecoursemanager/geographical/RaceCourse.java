package com.aayaffe.sailingracecoursemanager.geographical;

import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by aayaffe on 21/03/2016.
 */
public class RaceCourse {
    private int windDir;
    private double windSpeed;
    private BoatType boatType;
    private double boatLength;
    private double boatVMG;
    private int numOfBoats;//TODO Get all parameters in constructor.
    private int goalTime;
    private AviLocation signalBoatLoc;
    private boolean isUpdated; //TODO remove
    private RaceCourseType raceCourseType;
    private Marks marks = new Marks();
    private static Dictionary<String,RaceCourseType> raceCourseTypes = new Hashtable<>();
    private UUID uuid;

    private static void setRaceCourseTypes(){
        raceCourseTypes.put("Windward-leeward", RaceCourseType.WINDWARD_LEEWARD);
        raceCourseTypes.put("Trapezoid", RaceCourseType.TRAPEZOID);
        raceCourseTypes.put("Triangular", RaceCourseType.WINDWARD_LEEWARD_TRIANGLE);
    }

    public RaceCourse(){
        uuid = UUID.randomUUID();
        setRaceCourseTypes();
    }
    public RaceCourseType getRaceCourseType(String rc){
        return raceCourseTypes.get(rc);
    }




    public enum RaceCourseType{
        WINDWARD_LEEWARD_TRIANGLE,
        WINDWARD_LEEWARD,
        TRAPEZOID
    }
    public enum BoatType{ //TODO move to online database
        C470, C420, LASERRADIAL, LASERSTANDARD, LASER47, OPTIMISTINT, OPTIMISTLOCAL, RSX, BIC78, BIC68, BIC5, KITE, CATAMARAN17, CATAMARAN21, YACHT
    }

    public void calculateCourse(){ //TODO calculate everything automatically always
        marks = new Marks();
        addPinEndMark(getBoatLength(), numOfBoats, 1.5, signalBoatLoc, windDir); //TODO obtain details from DB
        AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength,numOfBoats,1.5));
        addMarks(rp, windDir);
    }

    private AviLocation findReferencePoint(AviLocation signalBoatLoc, int startLineLength) {
        return GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(GeoUtils.toLocation(signalBoatLoc), (float) GeoUtils.relativeToTrueDirection(windDir, -90), ((int) startLineLength / 2)));
    }

    private void addMarks(AviLocation rp, int windDir) {
        double nm = (GeoUtils.toHours(goalTime)*boatVMG)/2;
        int length = (int)(nm*1852);
        int dir = GeoUtils.relativeToTrueDirection(windDir, 0);
        AviLocation l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(rp.toLocation(), (float) dir, length));
        AviObject m = new AviObject();
        m.type = ObjectTypes.TriangleBuoy;
        m.name = "No1Mark";
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
        dir = GeoUtils.relativeToTrueDirection(windDir, 180);
        l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(rp.toLocation(), (float) dir, length));
        m = new AviObject();
        m.type = ObjectTypes.TomatoBuoy;
        m.name = "No4Mark";
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
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
        m.type = ObjectTypes.FlagBuoy;
        m.name = "PinEnd";
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
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
    public double getBoatLength() {
        return boatLength;
    }
    public void setBoatLength(double boatLength) {
        this.boatLength = boatLength;
    }
    public double getBoatVMG() {
        return boatVMG;
    }
    public void setBoatVMG(double boatVMG) {
        this.boatVMG = boatVMG;
    }
    public RaceCourseType getRaceCourseType() {
        return raceCourseType;
    }
    public void setRaceCourseType(RaceCourseType raceCourseType) {
        this.raceCourseType = raceCourseType;
    }
    public UUID getUuid() {
        return uuid;
    }

}
