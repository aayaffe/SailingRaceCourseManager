package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 21/03/2016.
 */
public class RaceCourse {
    private int windDir;
    private double windSpeed;
    private BoatType boatType;
    private double boatLength;
    private double boatVMGUpwind;
    private double boatVMGReach;
    private double boatVMGRun;
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

    public double getBoatVMGUpwind() {
        return boatVMGUpwind;
    }

    public void setBoatVMGUpwind(double boatVMGUpwind) {
        this.boatVMGUpwind = boatVMGUpwind;
    }

    public double getBoatVMGReach() {
        return boatVMGReach;
    }

    public void setBoatVMGReach(double boatVMGReach) {
        this.boatVMGReach = boatVMGReach;
    }

    public double getBoatVMGRun() {
        return boatVMGRun;
    }

    public void setBoatVMGRun(double boatVMGRun) {
        this.boatVMGRun = boatVMGRun;
    }


    public enum RaceCourseType{
        WINDWARD_LEEWARD_TRIANGLE,
        WINDWARD_LEEWARD,
        TRAPEZOID
    }
    public enum BoatType{ //TODO move to online database
        C470, C420, LASERRADIAL, LASERSTANDARD, LASER47, OPTIMISTINT, OPTIMISTLOCAL, RSX, BIC78, BIC68, BIC5, KITE, CATAMARAN17, CATAMARAN21, YACHT
    }

    public void calculateCourse(RaceCourseType rct){ //TODO calculate everything automatically always
        marks = new Marks();
        if (rct== RaceCourseType.WINDWARD_LEEWARD) {
            addPinEndMark(getBoatLength(), numOfBoats, 1.5, signalBoatLoc, windDir); //TODO obtain details from DB
            AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength, numOfBoats, 1.5));
            addWindwardLeewardMarks(rp, windDir);
        }
        if (rct== RaceCourseType.TRAPEZOID) {
            addPinEndMark(getBoatLength(), numOfBoats, 1.5, signalBoatLoc, windDir); //TODO obtain details from DB
            AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength, numOfBoats, 1.5));
            addTrapezoidMarks(rp, windDir);
        }
    }
//TODO add triangular
    private void addTrapezoidMarks(AviLocation rp, int windDir) {//TODO take into account course type (I1,I2,I3...)
        double nm = ((goalTime-0.1-0.05)/(getBoatVMGRun() + getBoatVMGUpwind()+ 0.5*getBoatVMGReach()));
        int length = (int)(nm*1852);
        int dir = GeoUtils.relativeToTrueDirection(windDir, 0);
        AviLocation l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(rp.toLocation(), (float) dir, (int)(0.05*1852)));
        AviObject m = new AviObject();
        m.type = ObjectTypes.TriangleBuoy;
        m.name = "No4Mark"; //TODO: Add gate mark
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
        dir = GeoUtils.relativeToTrueDirection(windDir, 0);
        l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(l.toLocation(), (float) dir, length));
        m = new AviObject();
        m.type = ObjectTypes.TriangleBuoy;
        m.name = "No1Mark"; //TODO: Add gate mark
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
        dir = GeoUtils.relativeToTrueDirection(windDir, -120);
        l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(l.toLocation(), (float) dir, (int)(0.5*length)));
        m = new AviObject();
        m.type = ObjectTypes.TriangleBuoy;
        m.name = "No2Mark";
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
        dir = GeoUtils.relativeToTrueDirection(windDir, 180);
        l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(l.toLocation(), (float) dir, (int)(length)));
        m = new AviObject();
        m.type = ObjectTypes.TriangleBuoy;
        m.name = "No3Mark"; //TODO: Add gate mark
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);
        dir = GeoUtils.relativeToTrueDirection(windDir, 120);
        l = GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(l.toLocation(), (float) dir,(int)(1852*0.1)));
        m = new AviObject();
        m.type = ObjectTypes.FlagBuoy;
        m.name = "FinishMark"; //TODO: Add finish boat an mark
        m.setAviLocation(l);
        m.color = "Orange";
        m.lastUpdate = new Date();
        m.setRaceCourseUUID(uuid);
        //TODO check setting ID - enter as mandatory to AVIObject constructor?
        marks.marks.add(m);

    }

    /***
     * Find the midposition of the start line
     * @param signalBoatLoc
     * @param startLineLength
     * @return
     */

    private AviLocation findReferencePoint(AviLocation signalBoatLoc, int startLineLength) {
        return GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(GeoUtils.toLocation(signalBoatLoc), (float) GeoUtils.relativeToTrueDirection(windDir, -90), ((int) startLineLength / 2)));
    }

    private void addWindwardLeewardMarks(AviLocation rp, int windDir) {
        double nm = (goalTime/(getBoatVMGRun() + getBoatVMGUpwind()))/2;
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
    private RaceCourseDescriptor getWindwardLeewardDescriptor() {
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartFinishLine);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Buoy);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(0, 0.34f));
        dds.add(new DirDist(180, 0.66f));
        List<String> names = new ArrayList<>();
        names.add("StartFinishline");
        names.add("No1Mark");
        names.add("No4Mark");
        return new RaceCourseDescriptor(os, dds, names);
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
