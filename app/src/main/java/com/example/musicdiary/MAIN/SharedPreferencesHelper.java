package com.example.musicdiary.MAIN;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "userData";
    private static final String KEY_USERID = "userID";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LAST_UPLOAD = "uploadDate";
    private static final String THEME = "Theme";
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

    public String getTheme(){
        return sharedPreferences.getString(THEME, "AppTheme.Blue");
    }

    public void setTheme(String Theme){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME, Theme);
        editor.apply();
    }


}
