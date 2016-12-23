package com.aayaffe.sailingracecoursemanager.geographical;

import android.graphics.Color;
import android.widget.ImageView;

/**
 * Created by aayaffe on 08/02/2016.
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
