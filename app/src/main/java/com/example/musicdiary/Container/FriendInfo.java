package com.example.musicdiary.Container;

public class FriendInfo {
    private String userID;
    private String username;
    private Long sinceTimestamp;

    public FriendInfo(String userID, String username, Long sinceTimestamp) {
        this.userID = userID;
        this.username = username;
        this.sinceTimestamp = sinceTimestamp;
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

    public Long getSinceTimestamp() {
        return sinceTimestamp;
    }

    public void setSinceTimestamp(Long sinceTimestamp) {
        this.sinceTimestamp = sinceTimestamp;
    }
}