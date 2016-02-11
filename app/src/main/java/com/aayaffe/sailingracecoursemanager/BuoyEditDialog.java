package com.aayaffe.sailingracecoursemanager;

/**
 * Created by aayaffe on 11/02/2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.aayaffe.sailingracecoursemanager.map.GoogleMapsActivity;

/**
 * Created by aayaffe on 09/02/2016.
 */
public class BuoyEditDialog extends DialogFragment implements View.OnClickListener {
    public long buoy_id;
    private Activity a;
    public static BuoyEditDialog newInstance(long id) {
        BuoyEditDialog frag = new BuoyEditDialog();
        Bundle args = new Bundle();
        args.putLong("buoy_id", id);
        frag.setArguments(args);
        return frag;
    }




    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface BuoyEditDialogListener {
        void onEditDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    BuoyEditDialogListener mDialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        a = activity;
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mDialogListener = (BuoyEditDialogListener) activity;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        buoy_id = getArguments().getLong("buoy_id");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.buoy_edit_dialog)
                .setTitle("Edit Buoy: "+buoy_id)
                        // Add action buttons
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BuoyEditDialog.this.getDialog().cancel();
                    }
                });
        LayoutInflater inflater = a.getLayoutInflater();
        View myview = inflater.inflate(R.layout.buoy_edit_dialog, null);
        Button addS = (Button) myview.findViewById (R.id.move_button);
        addS.setOnClickListener(this);

        Button minusS = (Button) myview.findViewById (R.id.delete_button);
        minusS.setOnClickListener(this);
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.move_button) {
            ((GoogleMapsActivity)a).onMoveButtonClick();
        }else{
            ((GoogleMapsActivity)a).onDeleteButtonClick();
        }
    }

}


