package com.shaheen.like4like.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FbPage implements Parcelable {

    String pageID;
    String userID;
    int points;
    int userTotalPoints;

    public FbPage() {
    }

    protected FbPage(Parcel in) {
        pageID = in.readString();
        userID = in.readString();
        points = in.readInt();
        userTotalPoints = in.readInt();
    }

    public static final Creator<FbPage> CREATOR = new Creator<FbPage>() {
        @Override
        public FbPage createFromParcel(Parcel in) {
            return new FbPage(in);
        }

        @Override
        public FbPage[] newArray(int size) {
            return new FbPage[size];
        }
    };

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getUserTotalPoints() {
        return userTotalPoints;
    }

    public void setUserTotalPoints(int userTotalPoints) {
        this.userTotalPoints = userTotalPoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pageID);
        dest.writeString(userID);
        dest.writeInt(points);
        dest.writeInt(userTotalPoints);
    }
}
