package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;

import java.util.Date;
import java.util.Objects;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class Object {
    public String name;
    public Location location;
    public ObjectTypes type;
    public String color;
    public Date lastUpdate;

    @Override
    public boolean equals(java.lang.Object o) {
        boolean result = false;
        if (o instanceof Object) {
            Object that = (Object) o;
            result = (that.canEqual(this) && Objects.equals(this.name,that.name));
        }
        return result;
    }
    public boolean canEqual(Object other) {
        return (other instanceof Object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
