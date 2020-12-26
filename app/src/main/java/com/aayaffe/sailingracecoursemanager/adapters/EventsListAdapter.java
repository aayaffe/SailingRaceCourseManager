package com.aayaffe.sailingracecoursemanager.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.activities.ChooseEventActivity;
import com.aayaffe.sailingracecoursemanager.db.IDBManager;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 14/01/2017.
 */

public class EventsListAdapter extends FirebaseListAdapter<Event> {
    private final IDBManager commManager;
    private final Users users;
    private final Activity mActivity;

    /**
     * @param activity    The activity containing the ListView

     * @param users
     */
    public EventsListAdapter(FirebaseListOptions options, Activity activity, IDBManager commManager, Users users) {
        super(options);
        this.mActivity = activity;
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
        final ImageView rc_flag = view.findViewById(R.id.rc_icon);
        final LinearLayout ll2 = view.findViewById(R.id.ll2);

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
            delete.setVisibility(View.GONE);
            delete.setEnabled(false);

        }
        if (manager!=null && manager.equals(users.getCurrentUser())){
            rc_flag.setVisibility(View.VISIBLE);
        }
        else{
            rc_flag.setVisibility(View.GONE);
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
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll15.getLayoutParams();
//            params.addRule(RelativeLayout.START_OF, R.id.ll2);
//            ll15.setLayoutParams(params);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll2.getLayoutParams();
//            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//            ll2.setLayoutParams(params);
        }
        else {
            viewOnly.setVisibility(View.GONE);
            viewOnly.setEnabled(false);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll2.getLayoutParams();
//            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//            ll2.setLayoutParams(params);
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
