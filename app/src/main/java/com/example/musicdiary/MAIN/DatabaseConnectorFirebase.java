package com.example.musicdiary.MAIN;

import androidx.annotation.NonNull;
import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.Container.Post;
import com.example.musicdiary.Container.UserInfo;
import com.google.firebase.database.*;

import java.util.*;

public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    public DatabaseConnectorFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // ========================== USER SETTERS ==========================

    // Add or update user entry
    public void addOrUpdateUser(String userID, String username) {
        UserInfo userInfo = new UserInfo(username);
        databaseReference.child("Users").child(userID).setValue(userInfo);
    }

    // Change username
    public void renameUser(String userID, String newUsername) {
        databaseReference.child("Users").child(userID).child("username").setValue(newUsername);

        // Optional: auch Author in Posts anpassen
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

    public void getUsernamesContaining(final String substring, final boolean ignoreCase, final UsernameListCallback callback) {
        databaseReference.child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> matches = new ArrayList<>();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            UserInfo user = userSnapshot.getValue(UserInfo.class);
                            if (user != null && user.getUsername() != null) {
                                String username = user.getUsername();
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

    // Holt zu einem Username die UID (und gibt Username+UID zurück)
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

    // OPTIONAL: Holt Username zu UID
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

    public void deleteUser(String userID) {
        databaseReference.child("Users").child(userID).removeValue();
        databaseReference.child("Posts").child(userID).removeValue();
        databaseReference.child("Friends").child(userID).removeValue();
    }

    // ========================== POST SETTERS / GETTERS / DELETERS ==========================

    public void addPost(String userID, Post post) { databaseReference.child("Posts").child(userID).push().setValue(post); }

    public interface PostCallback { void onCallback(Post post); }

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

    public void deleteAllPostsForUser(String UID) {
        databaseReference.child("Posts").child(UID).removeValue();
    }

    // ========================== FRIEND OPTIONS ==========================

    public void addFriend(String currentUserID, FriendInfo friendInfo) {
        databaseReference.child("Friends").child(currentUserID).child(friendInfo.getUserID()).setValue(friendInfo);
    }

    public void removeFriend(String currentUserID, String friendUserID) {
        databaseReference.child("Friends").child(currentUserID).child(friendUserID).removeValue();
    }

    public interface FriendListCallback { void onCallback(Map<String, FriendInfo> friends); }

    public void getFriendList(String currentUserID, final FriendListCallback callback) {
        databaseReference.child("Friends").child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, FriendInfo> friendMap = new HashMap<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            FriendInfo friendInfo = friendSnapshot.getValue(FriendInfo.class);
                            if (friendInfo != null)
                                friendMap.put(friendSnapshot.getKey(), friendInfo);
                        }
                        callback.onCallback(friendMap);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCallback(new HashMap<>());
                    }
                });
    }
}