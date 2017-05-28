package com.aayaffe.sailingracecoursemanager.communication;

import java.util.Date;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 19/03/2016.
 */
public interface CommManagerEventListener {


    void onConnect(Date time);

    void onDisconnect(Date time);

}


