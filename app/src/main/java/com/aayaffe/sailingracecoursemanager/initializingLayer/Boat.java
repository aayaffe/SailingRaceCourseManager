package com.aayaffe.sailingracecoursemanager;
/**
 * Created by Jonathan on 16/08/2016.
 */
/*
    represents a boat class
 */
public class Boat {
    private String name;  //class name
    private int targetTime;  //class default time
    private double[][] vmg = new double[4][3]; //speed^-1, minutes/NM //[5+,8+,12+,15+][upwind,downwind,reach]

    public Boat (String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public double[][] getVmg() {
        return vmg;
    }
    public void setVmg(double[][] vmg) {
        this.vmg = vmg;
    }
}

