package com.aayaffe.sailingracecoursemanager.Boats;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by aayaffe on 02/04/2016.
 */
public class BoatTypes {
    private String boatClass;
    private float length;
    private boolean windward_Leeward;
    private boolean triangular;
    private boolean trapezoid;
    private float upwind5_8;
    private float upwind8_12;
    private float upwind12_15;
    private float upwind15_;
    private float reach5_8;
    private float reach8_12;
    private float reach12_15;
    private float reach15_;
    private float run5_8;
    private float run8_12;
    private float run12_15;
    private float run15_;
    private long updated;


    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public boolean isWindward_Leeward() {
        return windward_Leeward;
    }

    public void setWindward_Leeward(boolean windward_Leeward) {
        this.windward_Leeward = windward_Leeward;
    }

    public boolean isTriangular() {
        return triangular;
    }

    public void setTriangular(boolean triangular) {
        this.triangular = triangular;
    }

    public boolean isTrapezoid() {
        return trapezoid;
    }

    public void setTrapezoid(boolean trapezoid) {
        this.trapezoid = trapezoid;
    }

    public float getUpwind5_8() {
        return upwind5_8;
    }

    public void setUpwind5_8(float upwind5_8) {
        this.upwind5_8 = upwind5_8;
    }

    public float getUpwind8_12() {
        return upwind8_12;
    }

    public void setUpwind8_12(float upwind8_12) {
        this.upwind8_12 = upwind8_12;
    }

    public float getUpwind12_15() {
        return upwind12_15;
    }

    public void setUpwind12_15(float upwind12_15) {
        this.upwind12_15 = upwind12_15;
    }

    public float getUpwind15_() {
        return upwind15_;
    }

    public void setUpwind15_(float upwind15_) {
        this.upwind15_ = upwind15_;
    }

    public float getReach5_8() {
        return reach5_8;
    }

    public void setReach5_8(float reach5_8) {
        this.reach5_8 = reach5_8;
    }

    public float getReach8_12() {
        return reach8_12;
    }

    public void setReach8_12(float reach8_12) {
        this.reach8_12 = reach8_12;
    }

    public float getReach12_15() {
        return reach12_15;
    }

    public void setReach12_15(float reach12_15) {
        this.reach12_15 = reach12_15;
    }

    public float getReach15_() {
        return reach15_;
    }

    public void setReach15_(float reach15_) {
        this.reach15_ = reach15_;
    }

    public float getRun5_8() {
        return run5_8;
    }

    public void setRun5_8(float run5_8) {
        this.run5_8 = run5_8;
    }

    public float getRun8_12() {
        return run8_12;
    }

    public void setRun8_12(float run8_12) {
        this.run8_12 = run8_12;
    }

    public float getRun12_15() {
        return run12_15;
    }

    public void setRun12_15(float run12_15) {
        this.run12_15 = run12_15;
    }

    public float getRun15_() {
        return run15_;
    }

    public void setRun15_(float run15_) {
        this.run15_ = run15_;
    }
    @Exclude
    public Date getUpdated() {
        return new Date(updated);
    }
    @Exclude
    public void setUpdated(Date updated) {

        this.updated = updated.getTime();
    }

    public String getBoatClass() {
        return boatClass;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }
}
