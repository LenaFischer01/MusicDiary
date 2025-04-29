package com.example.musicdiary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserIDManager {

    public static String getOrGenerateUserID(Context context) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
        String userID = helper.getUserID();

        if (userID == null) {
            userID = UUID.randomUUID().toString();
            helper.saveUserID(userID);
        }
        return userID;
    }
}