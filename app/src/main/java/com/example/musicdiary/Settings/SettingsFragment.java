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
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView displayUsername;
    private boolean isSpinnerInitialized = false;
    private int previousSpinnerPosition = -1;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        // Username IMMER direkt aus der DB holen, nicht (nur) lokal!
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        db.getUsernameByID(uid, (username, userId) -> {
            displayUsername.setText("Username: " + (username.isEmpty() ? "?" : username));
            // Option: helper.setName(username); // Sync lokalen Wert
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        displayUsername = view.findViewById(R.id.SettingsUsernameTextview);
        EditText inputUsername = view.findViewById(R.id.changeUsernameInput);
        Button checkUsername = view.findViewById(R.id.checkChangeUsernameButton);
        Button deleteAccount = view.findViewById(R.id.deleteAccountButton);

        initSpinner(view);

        checkUsername.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String newUsernameInput = inputUsername.getText() != null
                    ? inputUsername.getText().toString().trim() : "";

            if (newUsernameInput.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            // Leere und bereits existierende Namen verhindern:
            DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
            db.usernameExists(newUsernameInput, exists -> {
                if (exists) {
                    Toast.makeText(getContext(), "This username is already taken.", Toast.LENGTH_SHORT).show();
                } else {
                    // Nur neuen Namen updaten; Persistent Storage optional
                    db.renameUser(uid, newUsernameInput);
                    displayUsername.setText("Username: " + newUsernameInput);
                    new SharedPreferencesHelper(getContext()).setName(newUsernameInput); // Optional, für lokale Anzeige
                    Toast.makeText(getContext(), "Username changed!", Toast.LENGTH_SHORT).show();
                    inputUsername.setText("");
                }
            });
        });

        deleteAccount.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
            db.deleteUser(uid);

            new SharedPreferencesHelper(getContext()).setName("");
            new SharedPreferencesHelper(getContext()).saveUploadDate("");
            displayUsername.setText("Username: ");

            // Option: (Advanced) Logout/Neuanmeldung, um neue anonyme UID zu starten
            // FirebaseAuth.getInstance().signOut();
        });

        return view;
    }

    private void initSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.spinnerColortheme);

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
    public void onNothingSelected(AdapterView<?> parent) {}
}