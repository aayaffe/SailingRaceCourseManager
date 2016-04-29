package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by aayaffe on 26/04/2016.
 */
public class AviObjectTests {


    @Test
    public void equals_equal_ReturnsCorrect() {
        AviObject a = new AviObject();
        a.name = "a";
        a.setAviLocation(new AviLocation(0,0));
        AviObject a1 = new AviObject();
        a1.name = "a";
        a1.setAviLocation(new AviLocation(0,0));
        assertTrue(a.equals(a1));
    }
    @Test
    public void equals_notequal_ReturnsCorrect() {
        AviObject a = new AviObject();
        a.name = "a";
        a.setAviLocation(new AviLocation(0,0));
        AviObject b = new AviObject();
        b.name = "b";
        b.setAviLocation(new AviLocation(0,0));
        assertFalse(a.equals(b));
    }
    @Test
    public void equals_nameIsNull_ReturnsCorrect() {
        AviObject a = new AviObject();
        a.name = null;
        a.setAviLocation(new AviLocation(0,0));
        AviObject b = new AviObject();
        b.name = "b";
        b.setAviLocation(new AviLocation(0,0));
        assertFalse(a.equals(b));
        a = new AviObject();
        a.name = "a";
        a.setAviLocation(new AviLocation(0,0));
        b = new AviObject();
        b.name = null;
        b.setAviLocation(new AviLocation(0,0));
        assertFalse(a.equals(b));
    }

}
