package com.example.musicdiary.Settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;


public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    TextView displayUsername;
    private boolean isFirstSpinnerCall = true;
    private int previousSpinnerPosition = -1;
    private boolean isSpinnerInitialized = false;

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
        displayUsername.setText("Username: "+helper.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        displayUsername = (TextView) view.findViewById(R.id.SettingsUsernameTextview);
        EditText inputUsername = (EditText) view.findViewById(R.id.changeUsernameInput);

        Button checkUsername = (Button) view.findViewById(R.id.checkChangeUsernameButton);
        Button deleteAccount = (Button) view.findViewById(R.id.deleteAccountButton);

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getContext());

        initSpinner(view);

        checkUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername="";
                String userID = preferencesHelper.getUserID();
                if (inputUsername.getText() == null || inputUsername.getText().equals("")) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    newUsername = inputUsername.getText().toString().replace(" ","");
                }

                DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
                String finalNewUsername = newUsername;
                databaseConnectorFirebase.usernameExists(finalNewUsername, new DatabaseConnectorFirebase.UserExistsCallback() {
                    @Override
                    public void onCallback(boolean exists) {
                        if (exists){
                            Toast.makeText(getContext(), "This username is already taken.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // First change Username in sharedpreferences
                            preferencesHelper.setName(finalNewUsername);
                            // Change Username in Database
                            databaseConnectorFirebase.renameUser(userID, finalNewUsername);

                            // Change displayed name
                            displayUsername.setText("Username: "+ finalNewUsername);
                            Toast.makeText(getContext(), "Username changed!", Toast.LENGTH_SHORT).show();

                            //Clear input field
                            inputUsername.setText("");
                        }
                    }
                });
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
                databaseConnectorFirebase.deleteUser(preferencesHelper.getUserID());
                preferencesHelper.setName("");

                displayUsername.setText("Username: ");
            }
        });

        return view;
    }



    private void initSpinner(View view){
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerColortheme);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.themeColors,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        String themeName = helper.getTheme();
        String[] colorNames = getResources().getStringArray(R.array.themeColors);

        int pos = 0;
        for (int i = 0; i < colorNames.length; ++i) {
            if (themeName.endsWith(colorNames[i])) {
                pos = i;
                break;
            }
        }
        spinner.setSelection(pos, false);
        previousSpinnerPosition = pos;
        isSpinnerInitialized = true;
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isSpinnerInitialized) return;
        if (previousSpinnerPosition == position) return;
        previousSpinnerPosition = position;


        String color = (String) parent.getItemAtPosition(position);
        String themeName = "AppTheme." + color;
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        helper.setTheme(themeName);
        requireActivity().recreate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}