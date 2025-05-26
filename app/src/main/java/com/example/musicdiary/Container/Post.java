package com.example.musicdiary.Container;

/**
 * Represents a post containing content, a song and an author.
 */
public class Post {
    private String postContent;
    private String song;
    private String author;

    /**
     * Constructor to initialize post content, song and author.
     * Note: author is not assigned here (likely an oversight).
     * @param postContent Content of the post
     * @param song Associated song with the post
     * @param author Author of the post
     */
    public Post(String postContent, String song, String author) {
        this.postContent = postContent;
        this.song = song;
        // author is not initialized here in original code
    }

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Post.class)
     */
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

    /**
     * Empties the post content and song fields by setting them to empty strings.
     */
    public void emptyPost(){
        this.song = "";
        this.postContent = "";
    }
}