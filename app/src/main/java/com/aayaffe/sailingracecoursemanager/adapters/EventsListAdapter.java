package com.aayaffe.sailingracecoursemanager.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.activities.ChooseEventActivity;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 14/01/2017.
 */

public class EventsListAdapter extends FirebaseListAdapter<Event> {
    private final IDBManager commManager;
    private final Users users;

    /**
     * @param activity    The activity containing the ListView
     * @param modelClass  FirebaseDB will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single list item. You will be responsible for populating an
 *                    instance of the corresponding view with the data from an instance of modelClass.
     * @param ref         The FirebaseDB location to watch for data changes. Can also be a slice of a location, using some
*                    combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param users
     */
    public EventsListAdapter(Activity activity, Class<Event> modelClass, int modelLayout, Query ref, IDBManager commManager, Users users) {
        super(activity, modelClass, modelLayout, ref);
        this.commManager = commManager;
        this.users = users;
    }

    @Override
    protected void populateView(View view, final Event event, int position) {
        ((TextView)view.findViewById(android.R.id.text1)).setText(event.getName());
        String dates = getDateRangeString(event);
        User manager = commManager.findUser(event.getManagerUuid());
        final ImageButton delete =(ImageButton)view.findViewById(R.id.delete_event_button);
        final ImageButton viewOnly =(ImageButton)view.findViewById(R.id.view_only_event_button);

        if ((manager!=null && manager.equals(users.getCurrentUser()))||users.isAdmin(users.getCurrentUser())){
            delete.setVisibility(View.VISIBLE);
            delete.setEnabled(true);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ChooseEventActivity)mActivity).deleteEvent(event);

                }
            });
        }
        else {
            delete.setVisibility(View.INVISIBLE);
            delete.setEnabled(false);
        }
        if (users.isAdmin(users.getCurrentUser())){
            viewOnly.setVisibility(View.VISIBLE);
            viewOnly.setEnabled(true);
            viewOnly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ChooseEventActivity)mActivity).viewOnly(event);

                }
            });
        }
        else {
            viewOnly.setVisibility(View.INVISIBLE);
            viewOnly.setEnabled(false);
        }
        if (manager==null) {
            ((TextView) view.findViewById(android.R.id.text2)).setText(mActivity.getString(R.string.race_officer) + ": " +mActivity.getString(R.string.unknown));
        }
        else {
            ((TextView) view.findViewById(android.R.id.text2)).setText(mActivity.getString(R.string.race_officer) + ": " + manager.DisplayName);
        }
        ((TextView) view.findViewById(R.id.text3)).setText(dates);
    }
    @NonNull
    public static String getDateRangeString(Event event) {
        return getDateRangeString(event.yearStart,event.yearEnd,event.monthStart,event.monthEnd,event.dayStart,event.dayEnd);
    }

    public static String getDateRangeString(int yearStart, int yearEnd, int monthStart, int monthEnd, int dayStart, int dayEnd) {
        if ( dayEnd == 0 || yearStart == 0 || dayStart == 0 || yearEnd == 0)
            return "";
        if (dayStart == dayEnd && monthStart == monthEnd && yearStart == yearEnd)
            return String.valueOf(dayStart) + '/' + (monthStart+1) + '/' + yearStart;
        return String.valueOf(dayStart) + '/' + (monthStart+1) + '/' + yearStart + " - " + dayEnd + '/' + (monthEnd+1) + '/' + yearEnd;
    }
}
