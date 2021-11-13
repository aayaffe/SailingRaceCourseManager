package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aayaffe on 03/06/2017.
 */
public class RaceCourseStatisticsTest {
    @Test
    public void getSailTime() throws Exception {
        RaceCourseDescriptor rc = new InitialCourseDescriptor().getRaceCourse("Trapezoid 60\\120");
        Legs l = rc.getRaceCourseLegs().get(1); //Half beat
        Boat b = new Boat();
        double arr[][] ={{1,1,1},{2,3,4},{3,3,3},{4,4,4}};

        b.setVmg(arr);
        b.setBoatClass("TestClass1");
        b.setTargettime(12);
        assertEquals(12.5, RaceCourseTiming.GetSailTime(b,l,l.markRoundingOptions.get(0),1,10),0.0001);

    }

}