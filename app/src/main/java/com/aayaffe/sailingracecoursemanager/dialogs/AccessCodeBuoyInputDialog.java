package com.aayaffe.sailingracecoursemanager.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.aayaffe.sailingracecoursemanager.R;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 09/02/2016.
 */
public class AccessCodeBuoyInputDialog extends DialogFragment {
    private static final String TAG = "AccessCodeBuoyInputDialog";
    public String accessCode;
    private Context c;
    AccessCodeInputDialogListener mListener;

    public static AccessCodeBuoyInputDialog newInstance(Context c) {
        AccessCodeBuoyInputDialog frag = new AccessCodeBuoyInputDialog();
//        Bundle args = new Bundle();
//        args.putLong("buoyId", id);
//        frag.setArguments(args);
        frag.c = c;
        return frag;
    }

    public interface AccessCodeInputDialogListener {
        void onAccessCodeDialogPositiveClick(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AccessCodeInputDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(TAG,"Exception",e);
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = "Enter event access code";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_access_code_input, null);
        builder.setView(v)
                .setTitle(title)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAccessCodeDialogPositiveClick(AccessCodeBuoyInputDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AccessCodeBuoyInputDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

