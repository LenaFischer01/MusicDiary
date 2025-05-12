package com.example.musicdiary.MAIN;

import androidx.annotation.NonNull;

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.Container.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    public DatabaseConnectorFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    /* ==========================  USER SETTERS  ========================== */

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


    /* ==========================  USER GETTERS  ========================== */

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

    /**
     * Check if a username exists.
     * @param username The username to check.
     * @param callback Callback returns true if username exists, false otherwise.
     */
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

    public interface UsernameListCallback {
        void onCallback(java.util.List<String> usernames);
    }

    /**
     * Finds and returns all usernames that contain the given substring.
     * The search can optionally be case-insensitive.
     *
     * @param substring The text to look for within the usernames.
     * @param ignoreCase If true, the search will be case-insensitive.
     * @param callback Callback that receives a list of matching usernames (empty list if none found).
     */
    public void getUsernamesContaining(final String substring, final boolean ignoreCase, final UsernameListCallback callback) {
        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                java.util.List<String> matches = new java.util.ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getValue(String.class);
                    if (username != null) {
                        if (ignoreCase) {
                            if (username.toLowerCase().contains(substring.toLowerCase())) {
                                matches.add(username);
                            }
                        } else {
                            if (username.contains(substring)) {
                                matches.add(username);
                            }
                        }
                    }
                }
                callback.onCallback(matches);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(new java.util.ArrayList<>());
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Retrieves the username associated with a given userID from the database.
     *
     * @param userID   The unique identifier for the user whose data is to be retrieved.
     * @param callback The callback to return the username string. If user not found, returns null or empty string.
     */
    public void getUserDataByID(final String userID, final GetUserCallback callback) {
        DatabaseReference userRef = databaseReference.child("Users").child(userID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onCallback(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback("");
            }
        });
    }

    /**
     * Finds a userID associated with a given username by searching all users in the database.
     *
     * @param username The username (display name) to search for.
     * @param callback The callback to return the userID (as a string). If the username is not found, an empty string is returned.
     */
    public void getUserDataByName(final String username, final GetUserCallback callback) {
        DatabaseReference usersRef = databaseReference.child("Users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String UID = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String existingUsername = dataSnapshot.getValue(String.class);
                    if (existingUsername != null && existingUsername.equals(username)) {
                        UID = dataSnapshot.getKey();
                        break;
                    }
                }
                callback.onCallback(UID);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                callback.onCallback("");
            }
        });
    }

    /**
     * Callback interface for returning a user-related string value from an asynchronous database request.
     */
    public interface GetUserCallback {
        /**
         * Invoked when the requested user data is available.
         *
         * @param userString The retrieved user string (username or userID, depending on method).
         */
        void onCallback(String userString);
    }


    /* ==========================  USER DELETERS  ========================== */

    /**
     * Delete a user and all their posts from the database.
     * @param userID The unique identifier (key) for the user.
     */
    public void deleteUser(String userID) {
        databaseReference.child("Users").child(userID).removeValue();
        databaseReference.child("Posts").child(userID).removeValue();
    }


    /* ==========================  POST SETTERS  ========================== */

    /**
     * Add a post for a specific user.
     * @param userID The user identifier.
     * @param post The post object containing content and song.
     */
    public void addPost(String userID, Post post) {
        databaseReference.child("Posts").child(userID).push().setValue(post);
    }


    /* ==========================  POST GETTERS  ========================== */

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


    /* ==========================  POST DELETERS  ========================== */

    /**
     * Delete all posts for a specific user from the database.
     * @param UID The user identifier.
     */
    public void deleteAllPostsForUser(String UID) {
        DatabaseReference postRef = databaseReference.child("Posts").child(UID);
        postRef.removeValue();
    }

    /* ==========================  FRIEND OPTIONS  ========================== */

    /**
     * Add a friend entry for the current user.
     * The friendship timestamp is saved as server timestamp.
     * @param currentUserID The user adding a friend.
     * @param friendInfo The FriendInfo object of the friend to add.
     */
    public void addFriend(String currentUserID, FriendInfo friendInfo) {
        databaseReference.child("Friends").child(currentUserID).child(friendInfo.getUserID()).setValue(friendInfo);
    }

    /**
     * Remove friend entry for the current user.
     * @param currentUserID The user removing a friend.
     * @param friendUserID The userID of the friend to remove.
     */
    public void removeFriend(String currentUserID, String friendUserID) {
        // Remove the friend entry under currentUserID/friendUserID
        databaseReference.child("Friends").child(currentUserID).child(friendUserID).removeValue();
    }

    /**
     * Retrieve the list of friends of a specific user.
     * Callback returns a map of friendUserIDs to friendship info objects (e.g. since timestamp).
     * It will return a Map: K = UID of Friend; V = FriendInfo Object (username etc.).
     * @param currentUserID The user whose friends to get.
     * @param callback The callback to handle the results
     */
    public void getFriendList(String currentUserID, final FriendListCallback callback) {
        DatabaseReference friendsRef = databaseReference.child("Friends").child(currentUserID);
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, FriendInfo> friendMap = new HashMap<>();
                if (snapshot.exists()) {
                    for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                        FriendInfo friendInfo = friendSnapshot.getValue(FriendInfo.class);
                        if (friendInfo != null)
                            friendMap.put(friendSnapshot.getKey(), friendInfo);
                    }
                }
                callback.onCallback(friendMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(new HashMap<>());
            }
        });
    }

    /**
     * Callback interface for friend list retrieval.
     */
    public interface FriendListCallback {
        void onCallback(Map<String, FriendInfo> friends);
    }

}