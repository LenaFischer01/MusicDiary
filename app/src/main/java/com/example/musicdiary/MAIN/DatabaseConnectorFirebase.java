package com.example.musicdiary.MAIN;

import android.util.Log;

import androidx.annotation.NonNull;
import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.Container.Post;
import com.example.musicdiary.Container.UserInfo;
import com.google.firebase.database.*;

import java.util.*;

/**
 * Database connector for Firebase Realtime Database.
 * Handles operations related to users, posts, and friends.
 */
public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    /**
     * Constructor initializes the root database reference.
     */
    public DatabaseConnectorFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // ========================== USER SETTERS ==========================

    /**
     * Adds or updates a user entry with the given userID and username.
     * @param userID Unique identifier for the user.
     * @param username Username to add or update.
     */
    public void addOrUpdateUser(String userID, String username) {
        UserInfo userInfo = new UserInfo(username);
        databaseReference.child("Users").child(userID).setValue(userInfo);
    }

    /**
     * Changes the username for a user and optionally updates the author of posts.
     * @param userID The user's unique ID.
     * @param newUsername The new username to set.
     */
    public void renameUser(String userID, String newUsername) {
        databaseReference.child("Users").child(userID).child("username").setValue(newUsername);

        // Optional: also update author in Posts accordingly
        DatabaseReference postsRef = databaseReference.child("Posts").child(userID);
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setAuthor(newUsername);
                        postSnapshot.getRef().setValue(post);
                    }
                }
            }
            @Override public void onCancelled(DatabaseError error) { }
        });
    }

    // ========================== USER GETTERS ==========================

    public interface UserExistsCallback { void onCallback(boolean exists); }

    /**
     * Checks if a user exists by userID.
     * @param userID The user ID to check.
     * @param callback Callback to handle result (true if exists).
     */
    public void userExists(final String userID, final UserExistsCallback callback) {
        databaseReference.child("Users").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        callback.onCallback(dataSnapshot.exists());
                    }
                    @Override public void onCancelled(DatabaseError error) { callback.onCallback(false); }
                });
    }

    public interface UsernameListCallback { void onCallback(List<String> usernames); }

    /**
     * Checks if a username exists.
     * @param username The username to check.
     * @param callback Callback returns true if username exists.
     */
    public void usernameExists(final String username, final UserExistsCallback callback) {
        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserInfo user = userSnapshot.getValue(UserInfo.class);
                    if (user != null && user.getUsername().equals(username)) {
                        exists = true;
                        break;
                    }
                }
                callback.onCallback(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled: " + databaseError.getMessage());
                callback.onCallback(false);
            }
        });
    }

    /**
     * Retrieves usernames containing a given substring.
     * @param substring Substring to search in usernames.
     * @param ignoreCase True to ignore case while searching.
     * @param callback Callback returning list of matching usernames.
     */
    public void getUsernamesContaining(final String substring, final boolean ignoreCase, final UsernameListCallback callback) {
        databaseReference.child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> matches = new ArrayList<>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            UserInfo user = userSnapshot.getValue(UserInfo.class);
                            if (user != null && user.getUsername() != null) {
                                String username = user.getUsername();
                                Log.d("Test", "Prüfe UserSnapshot: " + user); // <- Sollte nicht null
                                if (ignoreCase) {
                                    if (username.toLowerCase().contains(substring.toLowerCase())) matches.add(username);
                                } else {
                                    if (username.contains(substring)) matches.add(username);
                                }
                            }
                        }
                        callback.onCallback(matches);
                    }
                    @Override public void onCancelled(DatabaseError error) { callback.onCallback(new ArrayList<>()); }
                });
    }

    public interface GetUserCallback { void onCallback(String userString, String userId); }

    /**
     * Gets user data (username and user ID) by username.
     * @param username The username to look up.
     * @param callback Callback returning username and user ID.
     */
    public void getUserDataByName(final String username, final GetUserCallback callback) {
        databaseReference.child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserInfo user = dataSnapshot.getValue(UserInfo.class);
                            if (user != null && user.getUsername().equals(username)) {
                                callback.onCallback(user.getUsername(), dataSnapshot.getKey());
                                return;
                            }
                        }
                        callback.onCallback("", ""); // not found
                    }
                    @Override public void onCancelled(DatabaseError error) { callback.onCallback("", ""); }
                });
    }

    /**
     * Optionally gets the username by user ID.
     * @param userID The user ID.
     * @param callback Callback returning the username and user ID.
     */
    public void getUsernameByID(final String userID, final GetUserCallback callback) {
        databaseReference.child("Users").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        UserInfo user = dataSnapshot.getValue(UserInfo.class);
                        if (user != null && user.getUsername() != null)
                            callback.onCallback(user.getUsername(), userID);
                        else
                            callback.onCallback("", userID);
                    }
                    @Override public void onCancelled(DatabaseError error) { callback.onCallback("", userID); }
                });
    }

    // ========================== USER DELETERS ==========================

    /**
     * Deletes user data, posts, and friend entries for the given user ID.
     * @param userID The user ID to delete.
     */
    public void deleteUser(String userID) {
        databaseReference.child("Users").child(userID).removeValue();
        databaseReference.child("Posts").child(userID).removeValue();
        databaseReference.child("Friends").child(userID).removeValue();
    }

    // ========================== POST SETTERS / GETTERS / DELETERS ==========================

    /**
     * Adds a post to the database under a user.
     * @param userID The user ID.
     * @param post The post to add.
     */
    public void addPost(String userID, Post post) {
        databaseReference.child("Posts").child(userID).push().setValue(post);
    }

    public interface PostCallback { void onCallback(Post post); }

    /**
     * Retrieves the latest post for a user.
     * @param userID The user ID.
     * @param callback Callback returning the post or null if none.
     */
    public void getPostForUser(String userID, final PostCallback callback) {
        DatabaseReference postRef = databaseReference.child("Posts").child(userID);
        postRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Post post = child.getValue(Post.class);
                        callback.onCallback(post);
                    }
                } else {
                    callback.onCallback(null);
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) { callback.onCallback(null); }
        });
    }

    /**
     * Deletes all posts for a user.
     * @param UID The user ID.
     */
    public void deleteAllPostsForUser(String UID) {
        databaseReference.child("Posts").child(UID).removeValue();
    }

    // ========================== FRIEND OPTIONS ==========================

    /**
     * Adds a friend entry under the current user.
     * @param currentUserID The current user's ID.
     * @param followingInfo FriendInfo object representing the friend.
     */
    public void addFriend(String currentUserID, FollowingInfo followingInfo) {
        databaseReference.child("Friends").child(currentUserID).child(followingInfo.getUserID()).setValue(followingInfo);
    }

    /**
     * Removes a friend from the current user's friend list.
     * @param currentUserID The current user's ID.
     * @param friendUserID The friend's user ID to remove.
     */
    public void removeFriend(String currentUserID, String friendUserID) {
        databaseReference.child("Friends").child(currentUserID).child(friendUserID).removeValue();
    }

    public interface FriendListCallback { void onCallback(Map<String, FollowingInfo> friends); }

    /**
     * Retrieves the friend list of the current user.
     * @param currentUserID The current user's ID.
     * @param callback Callback returning a map of friend IDs to FriendInfo.
     */
    public void getFriendList(String currentUserID, final FriendListCallback callback) {
        databaseReference.child("Friends").child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, FollowingInfo> friendMap = new HashMap<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            FollowingInfo followingInfo = friendSnapshot.getValue(FollowingInfo.class);
                            if (followingInfo != null)
                                friendMap.put(friendSnapshot.getKey(), followingInfo);
                        }
                        callback.onCallback(friendMap);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCallback(new HashMap<>());
                    }
                });
    }
}