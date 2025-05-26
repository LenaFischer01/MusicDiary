package com.example.musicdiary.Settings;

import android.content.Context;

import com.example.musicdiary.MAIN.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class UserIDManager {

    public static String getOrGenerateUserID(Context context) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(context);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userID == null) {
            userID = UUID.randomUUID().toString();
            helper.saveUserID(userID);
        }
        return userID;
    }
}