package com.example.musicdiary;

import com.example.musicdiary.Container.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    public DatabaseConnectorFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Add a new user to the database with a given userID and username.
     * @param userID Unique identifier for the user (key).
     * @param username Username (display name).
     */
    public void addUser(String userID, String username) {
        databaseReference.child("Users").child(userID).setValue(username);
    }

    /**
     * Rename a user by changing the username (not the userID) in the database.
     * Updates the username in /Users as well as the author property in all posts.
     * @param userID The unique identifier (key) for the user.
     * @param newUsername The new display name for the user.
     */
    public void renameUser(String userID, String newUsername) {
        // Update username in /Users/userID
        databaseReference.child("Users").child(userID).setValue(newUsername);

        // Optionally author in each post
        DatabaseReference postsRef = databaseReference.child("Posts").child(userID);
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null && post.getAuthor() != null) {
                            post.setAuthor(newUsername);
                            postSnapshot.getRef().setValue(post);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /**
     * Add a post for a specific user.
     * @param userID The user identifier.
     * @param post The post object containing content and song.
     */
    public void addPost(String userID, Post post) {
        databaseReference.child("Posts").child(userID).push().setValue(post);
    }

    /**
     * Delete a user and all their posts from the database.
     * @param userID The unique identifier (key) for the user.
     */
    public void deleteUser(String userID) {
        databaseReference.child("Users").child(userID).removeValue();
        databaseReference.child("Posts").child(userID).removeValue();
    }

    public interface UserExistsCallback {
        void onCallback(boolean exists);
    }

    /**
     * Check if a user exists.
     * @param userID The user identifier to check.
     * @param callback Callback returns true if user exists, false otherwise.
     */
    public void userExists(final String userID, final UserExistsCallback callback) {
        DatabaseReference userRef = databaseReference.child("Users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onCallback(dataSnapshot.exists());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(false);
            }
        });
    }

    public void usernameExists(final String username, final UserExistsCallback callback) {
        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String existingUsername = userSnapshot.getValue(String.class);
                    if (existingUsername != null && existingUsername.equals(username)) {
                        exists = true;
                        break;
                    }
                }
                callback.onCallback(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(false);
            }
        });
    }

    public interface PostCallback {
        void onCallback(Post post);
    }

    /**
     * Retrieve the latest post for a specified user from the database.
     * @param userID The user identifier.
     * @param callback Callback returns the most recent post for the user.
     */
    public void getPostForUser(String userID, final PostCallback callback) {
        DatabaseReference postRef = databaseReference.child("Posts").child(userID);
        postRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Post post = child.getValue(Post.class);
                        callback.onCallback(post);
                    }
                } else {
                    callback.onCallback(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(null);
            }
        });
    }

    /**
     * Delete all posts for a specific user from the database.
     * @param UID The user identifier.
     */
    public void deleteAllPostsForUser(String UID) {
        DatabaseReference postRef = databaseReference.child("Posts").child(UID);
        postRef.removeValue();
    }
}