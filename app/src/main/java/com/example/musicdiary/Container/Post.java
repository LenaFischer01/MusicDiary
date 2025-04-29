package com.example.musicdiary.Container;

public class Post {
    private String postContent;
    private String song;
    private String author;

    public Post(String postContent, String song, String author) {
        this.postContent = postContent;
        this.song = song;
    }

    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    public Post() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

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

    public void emptyPost(){
        this.song = "";
        this.postContent = "";
    }
}