package com.aayaffe.sailingracecoursemanager.communication;

import java.util.Date;

/**
 * Created by aayaffe on 19/03/2016.
 */
public interface CommManagerEventListener {


    void onConnect(Date time);

    void onDisconnect(Date time);

}


