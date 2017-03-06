package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.DistanceType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseException;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by aayaffe on 11/02/2017.
 */
public class LegsTest {
    private static double error = 0.0001;
    @Test
    public void getDistance() throws Exception, RaceCourseException {
        RaceCourseDescriptor2 rc = new TestCourseDescriptor().getRaceCourseDescriptors().get(0);
        double dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Absolute);
        assertEquals(2.5,dist,0.1);
        dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Relative);
        assertEquals(0.67,dist,0.01);
        dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Absolute);
        assertEquals(0.05,dist,0.01);
        dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Relative);
        assertEquals(2,dist,0.01);
        dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Absolute);
        assertEquals(0,dist,0.01);
        dist = rc.getRaceCourseLegs().get(0).GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Relative);
        assertEquals(2,dist,0.01);
    }
    @Test
    public void getDistanceTrapezoid60120ShortedOuter() throws RaceCourseException {
        RaceCourseDescriptor2 rc = new InitialCourseDescriptor().getRaceCourse("Trapezoid 60\\120");
        double dist;
        Legs legs;
        legs = rc.getRaceCourseLegs().get(0); //Shorted outer
        dist = legs.GetLength(rc.getRaceCourseLegs().get(0).defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Absolute);
        assertEquals(2.5,dist,0.1);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Relative);
        assertEquals(0.67,dist,0.01);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Absolute);
        assertEquals(0.05,dist,0.01);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Relative);
        assertEquals(2,dist,0.01);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Absolute);
        assertEquals(0,dist,0.01);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Relative);
        assertEquals(2,dist,0.01);
    }
    @Test
    public void getDistanceTrapezoid60120halfBeat() throws RaceCourseException {
        RaceCourseDescriptor2 rc = new InitialCourseDescriptor().getRaceCourse("Trapezoid 60\\120");
        double dist;
        Legs legs;
        legs = rc.getRaceCourseLegs().get(1); //Half beat
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Absolute);
        assertEquals(0.1,dist,error);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Reach, DistanceType.Relative);
        assertEquals(0.5,dist,error);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Absolute);
        assertEquals(0.05,dist,error);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.UpWind, DistanceType.Relative);
        assertEquals(2,dist,error);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Absolute);
        assertEquals(0,dist,error);
        dist = legs.GetLength(legs.defaultMarkRounding, Boat.PointOfSail.Run, DistanceType.Relative);
        assertEquals(2,dist,error);
    }



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