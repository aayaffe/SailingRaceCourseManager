package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import java.io.Serializable;

public class MarkLocation implements Serializable{
    public int direction; //Direction from last mark
    public double distance; // Distance from last mark (relative or NM)
    public boolean relativeDistance; //If true, distance is a multiplication of distance to M1
    public int fromMarkId;
    public LocationOptions locationOptions;


    public MarkLocation(int direction, double distance, boolean relativeDistance, LocationOptions locationOptions) {
        this.direction = direction;
        this.distance = distance;
        this.relativeDistance = relativeDistance;
        this.locationOptions = locationOptions;
        if (locationOptions==LocationOptions.FROM_MARK_ID)
            throw new IllegalArgumentException("Missing from mark id");
    }
    public MarkLocation(int direction, double distance, boolean relativeDistance, LocationOptions locationOptions, int fromMarkId) {
        this.direction = direction;
        this.distance = distance;
        this.relativeDistance = relativeDistance;
        this.locationOptions = locationOptions;
        this.fromMarkId = fromMarkId;
    }
}

