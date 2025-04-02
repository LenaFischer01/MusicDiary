package com.example.musicdiary.Container;

public class Post {
    public String postContent;
    public String song;

    public Post(String postContent, String song) {
        this.postContent = postContent;
        this.song = song;
    }

    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    public Post() {}

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}