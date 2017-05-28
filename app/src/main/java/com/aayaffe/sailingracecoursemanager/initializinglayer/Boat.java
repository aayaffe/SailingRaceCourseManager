
package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.google.firebase.database.Exclude;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 16/08/2016.
 */
public class Boat {
    private String boatClass;  //class boatClass
    private int targettime;  //class default time
    private double[][] vmg = new double[4][3]; //speed^-1, minutes/NM //[5+,8+,12+,15+][upwind,downwind,reach]
    public double reach12_15;
    public double reach15_;
    public double reach5_8;
    public double reach8_12;
    public double run12_15;
    public double run15_;
    public double run5_8;
    public double run8_12;
    public double upwind12_15;
    public double upwind15_;
    public double upwind5_8;
    public double upwind8_12;
    public double length;
    public boolean trapezoid;
    public boolean triangular;
    public boolean windward_Leeward;

    public Boat(){
        //For Firebase only
    }


    public Boat (String name){
        this.boatClass =name;
    }

    public String getBoatClass() {
        return boatClass;
    }
    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public int getTargettime() {
        return targettime;
    }

    public void setTargettime(int targettime) {
        this.targettime = targettime;
    }

    public double[][] getVmg() {
        vmg[0][0] = upwind5_8;
        vmg[1][0] = upwind8_12;
        vmg[2][0] = upwind12_15;
        vmg[3][0] = upwind15_;
        vmg[0][1] = run5_8;
        vmg[1][1] = run8_12;
        vmg[2][1] = run12_15;
        vmg[3][1] = run15_;
        vmg[0][2] = reach5_8;
        vmg[1][2] = reach8_12;
        vmg[2][2] = reach12_15;
        vmg[3][2] = reach15_;
        return vmg;
    }
    public void setVmg(double[][] vmg) {
        this.vmg = vmg;
    }

    public double getVmg(WindSpeed ws, PointOfSail pos){
        return getVmg()[ws.ordinal()][pos.ordinal()];
    }

    public double getVmg(double ws, PointOfSail pos){
        return getVmg(wind2Index(ws),pos);
    }

    @Exclude
    public static Boat.WindSpeed wind2Index(double wind){  //index the wind strength. knots to right index at the boat's vmg table.
        if (wind<8)
            return Boat.WindSpeed.WIND_SPEED5_8;
        else if (wind<=12)
            return Boat.WindSpeed.WIND_SPEED8_12;
        else if (wind<=15)
            return Boat.WindSpeed.WIND_SPEED12_15;
        return Boat.WindSpeed.WIND_SPEED15_;
    }

    @Exclude
    public static Boat.PointOfSail dir2PointOfSail(int windDir){
        if (windDir>310 || windDir<50)
            return PointOfSail.UpWind;
        else if ((windDir>=225 && windDir<=310)||(windDir>=50 && windDir<=135))
            return PointOfSail.Reach;
        return PointOfSail.Run;
    }

    public enum PointOfSail{
        UpWind , Run , Reach
    }
    public enum WindSpeed{
        WIND_SPEED5_8, WIND_SPEED8_12, WIND_SPEED12_15, WIND_SPEED15_
    }
}

