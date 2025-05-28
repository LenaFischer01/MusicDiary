package com.example.musicdiary.Container;

/**
 * Container class to hold a friend's username and their post for today.
 */
public class FollowingPostObject {
    private final String username;

    private final Post todaysPost;

    /**
     * Constructor initializing username and today's post.
     * @param username The friend's username
     * @param todaysPost The post of the friend for today
     */
    public FollowingPostObject(String username, Post todaysPost) {
        this.username = username;
        this.todaysPost = todaysPost;
    }

    /**
     * Returns the username of the friend.
     * @return username as String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns today's post of the friend.
     * @return todaysPost as Post object
     */
    public Post getTodaysPost() {
        return todaysPost;
    }
}