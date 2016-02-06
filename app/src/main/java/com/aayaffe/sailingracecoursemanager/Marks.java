package com.aayaffe.sailingracecoursemanager;

import android.support.v4.content.ContextCompat;

import com.aayaffe.sailingracecoursemanager.communication.*;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aayaffe on 31/10/2015.
 */
public class Marks {


    public List<AviObject> marks = new ArrayList<>();

    public int getIconID(AviObject ao){
        int ret = R.drawable.boatred;
        switch(ao.type) {
            case WorkerBoat: ret = R.drawable.boatcyan;
                break;
            case RaceManager: ret = R.drawable.managerblue;
                break;
            default: ret = R.drawable.boatred;
        }

//        if(ao.color.contains("blue")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatblue));
//        if(o.color.contains("cyan")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatcyan));
//        if(o.color.contains("orange")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatorange));
//        if(o.color.contains("green")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatgreen));
//        if(o.color.contains("pink")) b =  AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatpink));
//        if (o.type==ObjectTypes.RaceManager){
//            b =AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.managerblue));
//        }

//        if((o.lastUpdate!=null)&&(GeneralUtils.dateDifference(o.lastUpdate)>2000))
//        {
//            b = AndroidGraphicFactory.convertToBitmap(ContextCompat.getDrawable(MainActivity.this.getApplicationContext(), R.drawable.boatred));
//        }
        return ret;
    }
}
