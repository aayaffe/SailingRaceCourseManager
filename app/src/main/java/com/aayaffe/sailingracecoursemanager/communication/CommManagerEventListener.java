package com.aayaffe.sailingracecoursemanager.communication;

import java.util.Date;

/**
 * Created by aayaffe on 19/03/2016.
 */
public interface CommManagerEventListener {


    public void onConnect(Date time);

    public void onDisconnect(Date time);

}


