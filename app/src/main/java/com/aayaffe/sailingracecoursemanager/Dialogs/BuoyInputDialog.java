package com.aayaffe.sailingracecoursemanager.dialogs;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aayaffe.sailingracecoursemanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 09/02/2016.
 */
public class BuoyInputDialog extends DialogFragment {
    private static final String TAG = "BuoyInputDialog";
    public long buoy_id;
    private List<String> buoyTypes;
    private Context c;
    public static BuoyInputDialog newInstance(long id, List<String> buoyTypes, Context c) {
        BuoyInputDialog frag = new BuoyInputDialog();
        Bundle args = new Bundle();
        args.putLong("buoy_id", id);
        frag.setArguments(args);
        frag.buoyTypes = buoyTypes;
        frag.c = c;
        return frag;
    }

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
            Log.e(TAG,"Exception",e);
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buoy_id = getArguments().getLong("buoy_id",-1);
        String title = (buoy_id==-1)?"Add BUOY":"Edit BUOY: "+ buoy_id;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.buoy_input_dialog, null);
        builder.setView(v)
                .setTitle(title)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(BuoyInputDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BuoyInputDialog.this.getDialog().cancel();
                    }
                });
        Spinner s = (Spinner)v.findViewById(R.id.select_buoy_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, buoyTypes);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        return builder.create();
    }
}

