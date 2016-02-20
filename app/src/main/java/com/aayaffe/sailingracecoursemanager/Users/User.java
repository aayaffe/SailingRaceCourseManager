package com.aayaffe.sailingracecoursemanager.Users;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aayaffe on 17/02/2016.
 */
public class User implements Parcelable{
    public String Uid;
    public String DisplayName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Uid);
        dest.writeString(DisplayName);
    }
    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        Uid = in.readString();
        DisplayName = in.readString();
    }

    public User(){};
}
