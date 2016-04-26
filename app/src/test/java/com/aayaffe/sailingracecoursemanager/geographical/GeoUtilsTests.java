package com.aayaffe.sailingracecoursemanager.geographical;

import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by aayaffe on 26/04/2016.
 */
public class GeoUtilsTests {


        @Test
        public void toHours_CorrectInput_ReturnsCorrect() {
            assertThat(GeoUtils.toHours(360), is(6.0));
        }
}
