package com.example.musicdiary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicdiary.Container.Post;


public class SettingsFragment extends Fragment {

    TextView displayUsername;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        displayUsername.setText("Username: "+helper.getUsername());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        displayUsername = (TextView) view.findViewById(R.id.SettingsUsernameTextview);
        EditText inputUsername = (EditText) view.findViewById(R.id.changeUsernameInput);

        Button checkUsername = (Button) view.findViewById(R.id.checkChangeUsernameButton);

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getContext());

        checkUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername="";
                String finalOldUsername = preferencesHelper.getUsername();
                if (inputUsername.getText() == null || inputUsername.getText().equals("")) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    newUsername = inputUsername.getText().toString();
                }

                DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
                String finalNewUsername = newUsername;
                databaseConnectorFirebase.userExists(newUsername, new DatabaseConnectorFirebase.UserExistsCallback() {
                    @Override
                    public void onCallback(boolean exists) {
                        if (exists){
                            Toast.makeText(getContext(), "Username is already taken", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Write new username in sharedprefs and push username in db
                            databaseConnectorFirebase.addUser(finalNewUsername);

                            preferencesHelper.saveUsername(finalNewUsername);
                            Toast.makeText(getContext(), "Your username is now "+ finalNewUsername, Toast.LENGTH_SHORT).show();

                            databaseConnectorFirebase.addPost(finalOldUsername, new Post("#changedName "+finalNewUsername, " - "));

                            databaseConnectorFirebase.deleteUser(finalOldUsername);
                            inputUsername.setText("");
                            refreshData();
                        }
                    }
                });
            }
        });

        return view;
    }
}