package com.example.musicdiary.Container;

public class FriendPostObject {
    private final String username;

    private final Post todaysPost;

    public FriendPostObject(String username, Post todaysPost) {
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
