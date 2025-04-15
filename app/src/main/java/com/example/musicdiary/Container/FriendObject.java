package com.example.musicdiary.Container;

public class FriendObject {
    private final String username;

    private final Post todaysPost;

    public FriendObject(String username, Post todaysPost) {
        this.username = username;
        this.todaysPost = todaysPost;
    }

    public String getUsername() {
        return username;
    }

    public Post getTodaysPost() {
        return todaysPost;
    }
}
