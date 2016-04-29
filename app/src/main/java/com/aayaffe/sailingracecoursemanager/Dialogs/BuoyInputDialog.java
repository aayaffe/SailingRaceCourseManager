package com.aayaffe.sailingracecoursemanager.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.aayaffe.sailingracecoursemanager.R;

/**
 * Created by aayaffe on 09/02/2016.
 */
    public class BuoyInputDialog extends DialogFragment {
    public long buoy_id;
    private static Context c;
    public static BuoyInputDialog newInstance(long id, Context c) {
        BuoyInputDialog frag = new BuoyInputDialog();
        Bundle args = new Bundle();
        args.putLong("buoy_id", id);
        frag.setArguments(args);
        BuoyInputDialog.c = c;
        return frag;
    }
    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface BuoyInputDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events
    BuoyInputDialogListener mListener;
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (BuoyInputDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        buoy_id = getArguments().getLong("buoy_id",-1);
        String title = (buoy_id==-1)?"Add Buoy":"Edit Buoy: "+ buoy_id;

        // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.buoy_input_dialog, null);
            builder.setView(v)
            /*builder.setView(R.layout.buoy_input_dialog)*/
                    .setTitle(title)
                    // Add action buttons
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(BuoyInputDialog.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            BuoyInputDialog.this.getDialog().cancel();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

