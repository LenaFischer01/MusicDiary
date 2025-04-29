package com.example.musicdiary.Friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.SharedPreferencesHelper;

public class friendSearchFragment extends Fragment {

    EditText searchBar;

    public friendSearchFragment(){
        //
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.searchfriends, container, false);

        searchBar = view.findViewById(R.id.editTextSearchFriends);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get input as text
        String username = searchBar.getText().toString().replace(" ","");

        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());

        databaseConnectorFirebase.usernameExists(username, new DatabaseConnectorFirebase.UserExistsCallback() {
            @Override
            public void onCallback(boolean exists) {
                if (exists){
                    //TODO Make this work
                    // -> Add a Databasefunction to get the UID per username
                    //databaseConnectorFirebase.addFriend(helper.getUserID(), new FriendInfo("", username, ""));
                }
            }
        });

    }
}
