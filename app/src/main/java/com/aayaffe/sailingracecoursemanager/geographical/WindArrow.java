package com.aayaffe.sailingracecoursemanager.geographical;

import android.widget.ImageView;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 08/02/2016.
 */
public class WindArrow {
    private ImageView windArrow;

    public WindArrow(ImageView windArrow){
        this.windArrow = windArrow;
    }

    public void setDirection(Float d){
        windArrow.setRotation(d + 90);
    }
    public Float getDirection(){
        return windArrow.getRotation();
    }

}
