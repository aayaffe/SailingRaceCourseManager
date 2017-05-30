package com.aayaffe.sailingracecoursemanager.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.adapters.EventsListAdapter;
import com.aayaffe.sailingracecoursemanager.R;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 09/20/2016.
 */
public class EventInputDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "EventInputDialog";
    private View v;
    public String eventName;
    public int yearStart=0;
    public int yearEnd=0;
    public int monthStart=0;
    public int monthEnd=0;
    public int dayStart=0;
    public int dayEnd=0;
    private Context c;
    EventInputDialogListener mListener;
    private boolean datesSelected = false;

    public static EventInputDialog newInstance(String eventName, Context c) {
        EventInputDialog frag = new EventInputDialog();
        Bundle args = new Bundle();
        args.putString("eventName", eventName);
        frag.setArguments(args);
        frag.c = c;
        return frag;

    }



    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface EventInputDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        yearStart = year;
        this.yearEnd = yearEnd;
        monthStart=  monthOfYear;
        monthEnd = monthOfYearEnd;
        dayStart = dayOfMonth;
        dayEnd = dayOfMonthEnd;
        if (v!=null) {
            TextView dates = (TextView) v.findViewById(R.id.selectedDateRange);
            dates.setText(EventsListAdapter.getDateRangeString(year, yearEnd, monthOfYear, monthOfYearEnd, dayOfMonth, dayOfMonthEnd));
        }
    }


    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EventInputDialogListener) c;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(TAG,"Exception",e);
            throw new ClassCastException(c.toString()
                    + " must implement NoticeDialogListener");
        }
        eventName = getArguments().getString("eventName", "");
        String title = "Add new Event";//(buoyId==-1)?"Add BUOY":"Edit BUOY: "+ buoyId;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater)c.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.event_input_dialog, null);
        Button b = (Button) v.findViewById(R.id.selectDateRange);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (yearStart==0){
                    yearStart = now.get(Calendar.YEAR);
                    yearEnd = yearStart;
                    monthStart = now.get(Calendar.MONTH);
                    monthEnd = monthStart;
                    dayStart = now.get(Calendar.DAY_OF_MONTH);
                    dayEnd =  dayStart;
                }
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EventInputDialog.this,
                        yearStart,monthStart,dayStart,
                        yearEnd,monthEnd,dayEnd
                );
                dpd.setOnDateSetListener(EventInputDialog.this);
                dpd.show(getFragmentManager(), "Datepickerdialog");
                datesSelected=true;
            }
        });
        builder.setView(v)
        /*builder.setView(R.layout.event_input_dialog)*/
                .setTitle(title)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(EventInputDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EventInputDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        if (datesSelected){
            TextView dates = (TextView) v.findViewById(R.id.selectedDateRange);
            dates.setText(EventsListAdapter.getDateRangeString(yearStart,yearEnd,monthStart,monthEnd,dayStart,dayEnd));
        }
        return builder.create();
    }
}

