package com.example.musicdiary.Container;

public class FriendObject {
    private final String username;
    private final String todaysPostString;
    private final String todaysPostSong;

    public FriendObject(String username, String todaysPostString, String todaysPostSong) {
        this.username = username;
        this.todaysPostString = todaysPostString;
        this.todaysPostSong = todaysPostSong;
    }

    public String getUsername() {
        return username;
    }

    public String getTodaysPostString() {
        return todaysPostString;
    }

    public String getTodaysPostSong() {
        return todaysPostSong;
    }
}
