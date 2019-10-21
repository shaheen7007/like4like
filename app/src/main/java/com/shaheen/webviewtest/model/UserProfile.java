package com.shaheen.webviewtest.model;

public class UserProfile {
    private String userID;
    private String userEmail;
    private String userName;
    private int TotalPoints=0;
    private String fbPage;
    private int pointsPerLike;

    public String getListedPage() {
        return listedPage;
    }

    public void setListedPage(String listedPage) {
        this.listedPage = listedPage;
    }

    private String listedPage;

    public UserProfile() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getTotalPoints() {
        return TotalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        TotalPoints = totalPoints;
    }

    public String getFbPage() {
        return fbPage;
    }

    public void setFbPage(String fbPage) {
        this.fbPage = fbPage;
    }

    public int getPointsPerLike() {
        return pointsPerLike;
    }

    public void setPointsPerLike(int pointsPerLike) {
        this.pointsPerLike = pointsPerLike;
    }
}
