package com.aayaffe.sailingracecoursemanager.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: SailingRaceCourseManager
 * first created by aayaffe on 26/06/2018.
 */
public class AccessCodeShowDialog {
    private static final String TAG = "AccessCodeShowDialog";
    public static void showAccessCode(Context c, Event e) {
        Log.d(TAG, "Showing Access code");
        AlertDialog alertDialog = new AlertDialog.Builder(c).create();

        if (GeneralUtils.isNull(e,e.accessCode)) {
            alertDialog.setMessage("This event has no access code, and is therefor accessible to everyone");
        }
        else{
            alertDialog.setMessage("The access code to this event is: " + e.accessCode + "\nSend to the race personnel.");
        }
        alertDialog.setTitle("Access Code");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
