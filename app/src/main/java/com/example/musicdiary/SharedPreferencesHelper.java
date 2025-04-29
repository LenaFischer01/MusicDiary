package com.example.musicdiary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "userData";
    private static final String KEY_USERID = "userID";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FRIENDS = "friends";
    private static final String KEY_LAST_UPLOAD = "uploadDate";
    private SharedPreferences sharedPreferences;

    /**UserData = PREFS_NAME*/
    public SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setName(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getName(){
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public void saveUploadDate(String date){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_UPLOAD, date);
        editor.apply();
    }

    public String getUploadDAte(){
        return sharedPreferences.getString(KEY_LAST_UPLOAD, null);
    }

    /**
     * Saves the UserID.
     * @param userID ID that will be saved
     */
    public void saveUserID(String userID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERID, userID);
        editor.apply();
    }

    /**
     * returns UserID
     * @return UserID or Null.
     */
    public String getUserID() {
        return sharedPreferences.getString(KEY_USERID, null);
    }

    /**
     * Returns all saved friends (Their UIDs)
     * @return Set of friends / empty set
     */
    public Set<String> getFriends() {
        return sharedPreferences.getStringSet(KEY_FRIENDS, new HashSet<String>());
    }

    /**
     * Adds a friend to the list of stored friends.
     * @param friend The friend's ID to be added.
     */
    public void addFriend(String friend) {
        Set<String> friends = getFriends();
        friends.add(friend);
        saveFriends(friends);
    }

    /**
     * Saves the list of friends in SharedPreferences.
     * @param friends A set of friend names to be saved.
     */
    public void saveFriends(Set<String> friends) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_FRIENDS, friends);
        editor.apply();
    }

    /**
     * Deletes a friend from the list of stored friends.
     * @param friend The friend's name to be removed.
     */
    public void deleteFriend(String friend) {
        Set<String> friends = getFriends();
        friends.remove(friend);
        saveFriends(friends);
    }

}
