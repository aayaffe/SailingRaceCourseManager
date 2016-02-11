package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.Date;
import java.util.Objects;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class AviObject {
    public String name;
    public AviLocation location;
    public ObjectTypes type;
    public String color;
    public Date lastUpdate;
    public long id;
    @Override
    public boolean equals(java.lang.Object o) {
        boolean result = false;
        if (o instanceof AviObject) {
            AviObject that = (AviObject) o;
            result = (that.canEqual(this) && Objects.equals(this.name,that.name));
        }
        return result;
    }
    public boolean canEqual(AviObject other) {
        return (other instanceof AviObject);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
