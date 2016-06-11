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
            AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength, numOfBoats, 1.5));
            addWindwardLeewardMarks(rp, windDir, calculateStartLineLength(boatLength,numOfBoats, 1.5));
        }
        else if (rct== RaceCourseType.TRAPEZOID) {
            AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength, numOfBoats, 1.5));
            addTrapezoidMarks(rp, windDir, calculateStartLineLength(boatLength,numOfBoats, 1.5));
        }
        else if (rct== RaceCourseType.WINDWARD_LEEWARD_TRIANGLE) {
            AviLocation rp = findReferencePoint(signalBoatLoc, calculateStartLineLength(boatLength, numOfBoats, 1.5));
            addTriangularMarks(rp, windDir, calculateStartLineLength(boatLength,numOfBoats, 1.5));
        }
    }

    private void addTriangularMarks(AviLocation rp, int windDir, int startLineLength) {
        double nm = ((goalTime-0.1-0.05)/(getBoatVMGRun() + getBoatVMGUpwind()+ 0.5*getBoatVMGReach()));//TODO check race course length
        int length = (int)(nm*1852);
        SetRaceCourse(getTriangularDescriptor(rp,windDir,startLineLength,length));
    }

    private void addTrapezoidMarks(AviLocation rp, int windDir, int startLineLength) {//TODO take into account course type (I1,I2,I3...)
        double nm = ((goalTime-0.1-0.05)/(getBoatVMGRun() + getBoatVMGUpwind()+ 0.5*getBoatVMGReach()));//TODO check race course length
        int length = (int)(nm*1852);
        SetRaceCourse(getTrapezoidDescriptor(rp,windDir,startLineLength,length));
    }
    /***
     * Find the midposition of the start line
     * @param signalBoatLoc
     * @param startLineLength
     * @return
     */
    private AviLocation findReferencePoint(AviLocation signalBoatLoc, int startLineLength) {
        return GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(GeoUtils.toLocation(signalBoatLoc), (float) GeoUtils.relativeToTrueDirection(windDir, -90), (startLineLength / 2)));
    }
    private void addWindwardLeewardMarks(AviLocation rp, int windDir, int startLineLength) {

        double nm = (goalTime/(getBoatVMGRun() + getBoatVMGUpwind()));
        int length = (int)(nm*1852);
        RaceCourseDescriptor rcd = getWindwardLeewardDescriptor(rp,windDir,startLineLength,length);
        SetRaceCourse(rcd);

    }

    private void SetRaceCourse(RaceCourseDescriptor rcd) {
        if (null == rcd)
            return;
        for (RaceCourseObject rco: rcd){
            AviObject s = new AviObject();
            AviObject p = null;
            switch(rco.getType()){
                case Buoy:
                    s.setEnumType(ObjectTypes.TriangleBuoy);
                    s.name = rco.getName();
                    s.setAviLocation(rco.getLoc());
                    s.color = "Yellow";
                    s.lastUpdate = new Date();
                    s.setRaceCourseUUID(uuid);
                    break;
                case StartLine:
                case StartFinishLine: //TODO check removal of stbd mark\change it to a boat icon;
                    s.setEnumType(ObjectTypes.TriangleBuoy);
                    s.name = rco.getName()+"stbd";
                    s.setAviLocation(((RaceCourseObjectLong)rco).getStbLoc());
                    s.color = "Orange";
                    s.lastUpdate = new Date();
                    s.setRaceCourseUUID(uuid);
                    p = new AviObject();
                    p.setEnumType(ObjectTypes.FlagBuoy);
                    p.name = rco.getName()+"port";
                    p.setAviLocation(((RaceCourseObjectLong)rco).getPrtLoc());
                    p.color = "Blue";
                    p.lastUpdate = new Date();
                    p.setRaceCourseUUID(uuid);
                    break;
                case FinishLine:
                    s.setEnumType(ObjectTypes.TriangleBuoy); //TODO check to set a diffrent icon to stbd side
                    s.name = rco.getName()+"Stbd";
                    s.setAviLocation(((RaceCourseObjectLong)rco).getStbLoc());
                    s.color = "Orange";
                    s.lastUpdate = new Date();
                    s.setRaceCourseUUID(uuid);
                    p = new AviObject();
                    p.setEnumType(ObjectTypes.FlagBuoy);
                    p.name = rco.getName()+"Port";
                    p.setAviLocation(((RaceCourseObjectLong)rco).getPrtLoc());
                    p.color = "Orange";
                    p.lastUpdate = new Date();
                    p.setRaceCourseUUID(uuid);
                    break;
                case Gate:
                    s.setEnumType(ObjectTypes.TomatoBuoy);
                    s.name = rco.getName()+"Stbd";
                    s.setAviLocation(((RaceCourseObjectLong)rco).getStbLoc());
                    s.color = "Red";
                    s.lastUpdate = new Date();
                    s.setRaceCourseUUID(uuid);
                    p = new AviObject();
                    p.setEnumType(ObjectTypes.TomatoBuoy);
                    p.name = rco.getName()+"Port";
                    p.setAviLocation(((RaceCourseObjectLong)rco).getPrtLoc());
                    p.color = "Red";
                    p.lastUpdate = new Date();
                    p.setRaceCourseUUID(uuid);
                    break;
                case ReferencePoint:
                default:
                    break;

            }
            if(s!=null) {
                marks.marks.add(s);
            }
            if(p!=null){
                marks.marks.add(p);
            }

        }
    }

    private RaceCourseDescriptor getWindwardLeewardDescriptor(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength) {
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
        return new RaceCourseDescriptor(os, dds, names,startlineLoc,windDir,startLineLength,commonLength);
    }
    private RaceCourseDescriptor getTrapezoidDescriptor(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength) {
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartFinishLine);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.FinishLine);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(0, 95));
        dds.add(new DirDist(0, 0.33f));
        dds.add(new DirDist(240, 0.1666f));
        dds.add(new DirDist(180, 0.33f));
        dds.add(new DirDist(120, 185));
        List<String> names = new ArrayList<>();
        names.add("StartLine");
        names.add("No4Gate");
        names.add("No1Mark");
        names.add("No2Mark");
        names.add("No3Gate");
        names.add("FinishLine");
        return new RaceCourseDescriptor(os, dds, names,startlineLoc,windDir,startLineLength,commonLength);
    }
    private RaceCourseDescriptor getTriangularDescriptor(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength) {
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartFinishLine);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Buoy);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(0, 95));
        dds.add(new DirDist(0, 0.414213562f));
        dds.add(new DirDist(225, 0.292893218f));

        List<String> names = new ArrayList<>();
        names.add("StartFinishLine");
        names.add("No3Mark");
        names.add("No1Mark");
        names.add("No2Mark");
        return new RaceCourseDescriptor(os, dds, names,startlineLoc,windDir,startLineLength,commonLength);
    }
    public int calculateTargetSpeed(BoatType bt){
        //TODO Implement
        return 0;
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

}
