package com.example.musicdiary.MAIN;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class to manage shared preferences related to user data and app settings.
 */
public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "userData";
    private static final String KEY_USERID = "userID";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LAST_UPLOAD = "uploadDate";
    private static final String THEME = "Theme";
    private SharedPreferences sharedPreferences;

    /**
     * Constructor initializes SharedPreferences with the given context.
     * @param context Application context used to access SharedPreferences.
     */
    public SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Saves the username to SharedPreferences.
     * @param username The username to save.
     */
    public void setName(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * Retrieves the stored username.
     * @return Stored username, or null if not set.
     */
    public String getName(){
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * Saves the last upload date (e.g., post upload date).
     * @param date The date string to save.
     */
    public void saveUploadDate(String date){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_UPLOAD, date);
        editor.apply();
    }

    /**
     * Retrieves the last upload date.
     * @return The saved upload date string, or null if none.
     */
    public String getUploadDAte(){
        return sharedPreferences.getString(KEY_LAST_UPLOAD, null);
    }

    /**
     * Saves the user ID to SharedPreferences.
     * @param userID The user ID to save.
     */
    public void saveUserID(String userID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERID, userID);
        editor.apply();
    }

    /**
     * Retrieves the saved theme name.
     * @return The name of the theme, defaults to "AppTheme.Blue".
     */
    public String getTheme(){
        return sharedPreferences.getString(THEME, "AppTheme.Blue");
    }

    /**
     * Sets the app theme name to be saved.
     * @param theme The theme name to save.
     */
    public void setTheme(String theme){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME, theme);
        editor.apply();
    }
}