package com.example.musicdiary.Container;

public class UserInfo {
    private String username;

    public UserInfo() {}
    public UserInfo(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}