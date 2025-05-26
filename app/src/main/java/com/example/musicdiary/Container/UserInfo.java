package com.example.musicdiary.Container;

/**
 * Container class to store user information.
 */
public class UserInfo {
    private String username;

    /**
     * Default constructor.
     */
    public UserInfo() {}

    /**
     * Constructor initializing the username.
     * @param username The username of the user
     */
    public UserInfo(String username) {
        this.username = username;
    }

    /**
     * Returns the username.
     * @return username as String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}