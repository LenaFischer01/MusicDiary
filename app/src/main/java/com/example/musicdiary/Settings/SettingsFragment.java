package com.example.musicdiary.Settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
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
import com.example.musicdiary.MAIN.LoginActivity;
import com.example.musicdiary.R;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SettingsFragment allows user to view and change username,
 * select app theme, delete their account and logout.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener, DeleteAccountDialog.DeleteAccountDialogListener, LogoutDialogFragment.LogoutDialogListener{

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

    /**
     * Refresh the displayed username by retrieving it directly from the database.
     */
    private void refreshData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        db.getUsernameByID(uid, (username, userId) -> {
            displayUsername.setText("Username: " + (username.isEmpty() ? "?" : username));
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
        Button logout = view.findViewById(R.id.logoutButton);

        initSpinner(view);

        checkUsername.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String newUsernameInput = inputUsername.getText() != null
                    ? inputUsername.getText().toString().trim() : "";

            if (newUsernameInput.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prevent empty or already existing names
            DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
            db.usernameExists(newUsernameInput, exists -> {
                if (exists) {
                    Toast.makeText(getContext(), "This username is already taken.", Toast.LENGTH_SHORT).show();
                } else {
                    // Update to new name only; persistent storage optionally updated
                    db.renameUser(uid, newUsernameInput);
                    displayUsername.setText("Username: " + newUsernameInput);
                    new SharedPreferencesHelper(getContext()).setName(newUsernameInput); // Optional, for local display
                    Toast.makeText(getContext(), "Username changed!", Toast.LENGTH_SHORT).show();
                    inputUsername.setText("");
                }
            });
        });

        deleteAccount.setOnClickListener(v -> {
            DeleteAccountDialog dialog = DeleteAccountDialog.newInstance();
            dialog.setListener(this);
            dialog.show(getParentFragmentManager(), "");
        });

        logout.setOnClickListener(v -> {
            LogoutDialogFragment dialogFragment = LogoutDialogFragment.newInstance();
            dialogFragment.setListener(this);
            dialogFragment.show(getParentFragmentManager(), "");
        });

        return view;
    }

    /**
     * Initialize the theme selection spinner and set its listener.
     * @param view The fragment's root view.
     */
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
        // Prevent triggering listener during initialization or on unchanged selection
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

    /**
     * Callback when user confirms account deletion.
     * Deletes user data from database and Firebase Authentication, then redirects to login.
     */
    @Override
    public void onDialogSubmit() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        db.deleteUser(uid);

        new SharedPreferencesHelper(getContext()).setName("");
        new SharedPreferencesHelper(getContext()).saveUploadDate("");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getContext(), LoginActivity.class));
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Callback when user confirms logout.
     * Clears local preferences, signs out from Firebase, and returns to login screen.
     */
    @Override
    public void onDialogSubmitLogout() {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        helper.setName("");
        helper.saveUploadDate("");
        helper.setTheme("");

        AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(task -> {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    requireActivity().finish();
                });
    }
}