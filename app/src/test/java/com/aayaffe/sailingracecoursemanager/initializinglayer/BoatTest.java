package com.aayaffe.sailingracecoursemanager.initializinglayer;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by aayaffe on 18/02/2017.
 */
public class BoatTest {
    @Test
    public void dir2PointOfSail() throws Exception {
        Assert.assertEquals(Boat.PointOfSail.UpWind,Boat.dir2PointOfSail(49));
        Assert.assertEquals(Boat.PointOfSail.UpWind,Boat.dir2PointOfSail(311));
        Assert.assertEquals(Boat.PointOfSail.UpWind,Boat.dir2PointOfSail(0));
        Assert.assertEquals(Boat.PointOfSail.UpWind,Boat.dir2PointOfSail(360));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(225));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(310));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(50));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(135));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(300));
        Assert.assertEquals(Boat.PointOfSail.Reach,Boat.dir2PointOfSail(75));
        Assert.assertEquals(Boat.PointOfSail.Run,Boat.dir2PointOfSail(136));
        Assert.assertEquals(Boat.PointOfSail.Run,Boat.dir2PointOfSail(224));
        Assert.assertEquals(Boat.PointOfSail.Run,Boat.dir2PointOfSail(180));
    }

}