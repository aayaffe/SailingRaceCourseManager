package com.aayaffe.sailingracecoursemanager.dialogs;

import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.InitialCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseStatistics;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aayaffe on 06/06/2017.
 */
public class DistanceDialogTest {
    @Test
    public void calcDistByClassWind() throws Exception {
        RaceCourseDescriptor rc = new InitialCourseDescriptor().getRaceCourse("Trapezoid 60\\120");
        Legs l = rc.getRaceCourseLegs().get(1); //Half beat
        Boat b = new Boat();
        double arr[][] ={{1,1,1},{2,3,4},{3,3,3},{4,4,4}};

        b.setVmg(arr);
        b.setBoatClass("TestClass1");
        b.setTargettime(12);
        DistanceDialog dd = new DistanceDialog(null, null);
        assertEquals(4.125, dd.calcDistByClassWind(b,10,50,l),0.0001);
    }

}