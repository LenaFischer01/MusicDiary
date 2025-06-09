package com.example.musicdiary.Container;

/**
 * Container class to store information about a friend.
 */
public class FollowingInfo {
    private String userID;
    private String sinceTimestamp;

    /**
     * Constructor with parameters to initialize all fields.
     * @param userID Unique identifier of the user
     * @param sinceTimestamp Timestamp indicating since when the friendship exists
     */
    public FollowingInfo(String userID, String sinceTimestamp) {
        this.userID = userID;
        this.sinceTimestamp = sinceTimestamp;
    }

    /**
     * Empty constructor, required otherwise the app always crashes...
     */
    public FollowingInfo(){
        // Empty constructor, otherwise the app always crashes...
    }

    // Getter and setter methods for userID and sinceTimestamp

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSinceTimestamp() {
        return sinceTimestamp;
    }

    public void setSinceTimestamp(String sinceTimestamp) {
        this.sinceTimestamp = sinceTimestamp;
    }
}