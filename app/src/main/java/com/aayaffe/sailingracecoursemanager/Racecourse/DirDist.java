package com.aayaffe.sailingracecoursemanager.Racecourse;

/**
 * Created by aayaffe on 24/04/2016.
 */
public class DirDist {
    public float distFractionional;
    public int direction;
    public int distAbsolute;
    public boolean isDistAbsolute;

    public DirDist(int dir, float distFrac){
        isDistAbsolute = false;
        direction = dir;
        distFractionional = distFrac;
    }

    public DirDist(int dir, int distAbs){
        isDistAbsolute = true;
        direction = dir;
        distAbsolute = distAbs;
    }

}
