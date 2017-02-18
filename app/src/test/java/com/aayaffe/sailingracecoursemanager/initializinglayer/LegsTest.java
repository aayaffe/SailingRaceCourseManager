package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by aayaffe on 11/02/2017.
 */
public class LegsTest {
    @Test
    public void parseBuoys() throws Exception {
        RaceCourseDescriptor2 rc = new TestCourseDescriptor().getRaceCourseDescriptors().get(0);
        AviLocation rcLoc = new AviLocation();
        rcLoc.setLon(0).setLat(0);
        double dist2M1 = 1;
        int windDir = 0;
        float startLineLength = 150;
        UUID uuid = UUID.randomUUID();

        //rc.getRaceCourseLegs().get(0).parseBuoys(rcLoc,)

    }

}