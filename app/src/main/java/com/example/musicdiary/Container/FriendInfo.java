package com.example.musicdiary.Container;

public class FriendInfo {
    private String userID;
    private String username;
    private String sinceTimestamp;

    public FriendInfo(String userID, String username, String sinceTimestamp) {
        this.userID = userID;
        this.username = username;
        this.sinceTimestamp = sinceTimestamp;
    }

    public FriendInfo(){
        // Empty constructor or else the app always crashes...
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSinceTimestamp() {
        return sinceTimestamp;
    }

    public void setSinceTimestamp(String sinceTimestamp) {
        this.sinceTimestamp = sinceTimestamp;
    }
}